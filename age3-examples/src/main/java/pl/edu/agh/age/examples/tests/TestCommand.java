/*
 * Copyright (C) 2016 Intelligent Information Systems Group.
 *
 * This file is part of AgE.
 *
 * AgE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgE.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.agh.age.examples.tests;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.nonNull;
import static pl.edu.agh.age.services.worker.internal.SpringConfiguration.fromFilesystem;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.console.command.BaseCommand;
import pl.edu.agh.age.examples.SimpleLongRunning;
import pl.edu.agh.age.examples.SimpleLongRunningWithError;
import pl.edu.agh.age.services.lifecycle.LifecycleMessage;
import pl.edu.agh.age.services.lifecycle.internal.DefaultNodeLifecycleService;
import pl.edu.agh.age.services.worker.WorkerMessage;
import pl.edu.agh.age.services.worker.internal.DefaultWorkerService;
import pl.edu.agh.age.services.worker.internal.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.SpringConfiguration;
import pl.edu.agh.age.services.worker.internal.WorkerConfiguration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command that eases testing the cluster.
 */
@Named
@Scope("prototype")
@Parameters(commandNames = "test", commandDescription = "Run sample computations", optionPrefixes = "--")
public final class TestCommand extends BaseCommand {

	private enum Operation {
		LIST_EXAMPLES("list-examples"),
		EXECUTE("execute"),
		COMPUTATION_INTERRUPTED("computation-interrupted");

		private final String operationName;

		Operation(final @NonNull String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}
	}

	private enum Type {
		DESTROY("destroy"),
		COMPUTE_ERROR("compute-error"),
		NODE_ERROR("node-error");

		private final String typeName;

		Type(final @NonNull String typeName) {
			this.typeName = typeName;
		}

		public String typeName() {
			return typeName;
		}
	}

	private static final String EXAMPLES_PACKAGE = "pl.edu.agh.age.examples";

	private static final Logger log = LoggerFactory.getLogger(TestCommand.class);

	@Inject private WorkerServiceClient workerServiceClient;

	@Inject private LifecycleServiceClient lifecycleServiceClient;

	@Parameter(names = "--example") private @MonotonicNonNull String example;

	@Parameter(names = "--config") private @MonotonicNonNull String config;

	@Parameter(names = "--type") private @MonotonicNonNull String type = Type.DESTROY.typeName();

	public TestCommand() {
		addHandler(Operation.LIST_EXAMPLES.operationName(), this::listExamples);
		addHandler(Operation.EXECUTE.operationName(), this::executeExample);
		addHandler(Operation.COMPUTATION_INTERRUPTED.operationName(), this::computationInterrupted);
	}

	@Override public Set<String> operations() {
		return Arrays.stream(Operation.values()).map(Operation::operationName).collect(Collectors.toSet());
	}

	private void listExamples(final Terminal terminal) {
		log.debug("Listing examples.");
		try {
			final ClassPath classPath = ClassPath.from(TestCommand.class.getClassLoader());
			final ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(EXAMPLES_PACKAGE);
			log.debug("Class path {}.", classes);
			classes.forEach(klass -> terminal.writer().println(klass.getSimpleName()));
		} catch (final IOException e) {
			log.error("Cannot load classes.", e);
			terminal.writer().println("Error: Cannot load classes.");
		}
	}

	private void executeExample(final Terminal terminal) {
		final WorkerConfiguration configuration;
		try {
			if (nonNull(config)) {
				configuration = runConfig();
			} else if (nonNull(example)) {
				configuration = runExample();
			} else {
				terminal.writer().println("Provide --config or --example.");
				return;
			}
		} catch (final IOException e) {
			terminal.writer().println("File " + config + " does not exist.");
			return;
		}

		try {
			TimeUnit.SECONDS.sleep(1L);
			log.debug("Sending {}", configuration);
			workerServiceClient.prepareConfiguration(configuration);
			TimeUnit.SECONDS.sleep(1L);
			workerServiceClient.startComputation();
		} catch (final InterruptedException e) {
			log.debug("Interrupted.", e);
		}
	}

	private SingleClassConfiguration runExample() {
		log.debug("Executing example.");
		final String className = EXAMPLES_PACKAGE + '.' + example;

		return new SingleClassConfiguration(className);
	}

	private SpringConfiguration runConfig() throws IOException {
		log.debug("Running config.");
		return SpringConfiguration.fromFilesystem(config);
	}

	/**
	 * Operation to test interrupted computation.
	 *
	 * Currently it can run:
	 * # a computation stopped by cluster destruction,
	 * # a computation stopping because of its own error.
	 *
	 * @param terminal
	 * 		Print writer.
	 */
	private void computationInterrupted(final Terminal terminal) {
		log.debug("Testing interrupted computation.");

		terminal.writer().println("Loading class...");
		final String className = type.equals(Type.COMPUTE_ERROR.typeName())
		                         ? SimpleLongRunningWithError.class.getCanonicalName()
		                         : SimpleLongRunning.class.getCanonicalName();
		try {
			workerServiceClient.prepareConfiguration(new SingleClassConfiguration(className));
			terminal.writer().println("Starting computation...");
			workerServiceClient.startComputation();
			terminal.writer().println("Waiting...");

			TimeUnit.SECONDS.sleep(10L);
		} catch (final InterruptedException e) {
			log.debug("Interrupted.", e);
		}

		if (type.equals(Type.DESTROY.typeName())) {
			terminal.writer().println("Destroying cluster...");
			lifecycleServiceClient.destroyCluster();
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
