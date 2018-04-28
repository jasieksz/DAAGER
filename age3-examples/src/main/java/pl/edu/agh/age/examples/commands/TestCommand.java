/*
 * Copyright (C) 2016-2018 Intelligent Information Systems Group.
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
package pl.edu.agh.age.examples.commands;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.console.command.Command.getAndCast;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.console.command.Command;
import pl.edu.agh.age.console.command.Operation;
import pl.edu.agh.age.console.command.Parameter;
import pl.edu.agh.age.examples.SimpleLongRunning;
import pl.edu.agh.age.examples.SimpleLongRunningWithError;
import pl.edu.agh.age.services.worker.internal.configuration.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command that eases testing the cluster.
 */
@Named
public final class TestCommand implements Command {
	private static final Logger logger = LoggerFactory.getLogger(TestCommand.class);

	private enum Type {
		DESTROY("destroy"),
		COMPUTE_ERROR("compute-error"),
		NODE_ERROR("node-error");

		private final String typeName;

		Type(final String typeName) {
			this.typeName = typeName;
		}

		public String typeName() {
			return typeName;
		}
	}

	private static final String EXAMPLES_PACKAGE = "pl.edu.agh.age.examples";

	private final WorkerServiceClient workerServiceClient;

	private final LifecycleServiceClient lifecycleServiceClient;

	private final PrintWriter writer;

	@Inject
	public TestCommand(final WorkerServiceClient workerServiceClient,
	                   final LifecycleServiceClient lifecycleServiceClient, final Terminal terminal) {
		this.workerServiceClient = requireNonNull(workerServiceClient);
		this.lifecycleServiceClient = requireNonNull(lifecycleServiceClient);
		writer = terminal.writer();
	}

	@Override public String name() {
		return "test";
	}

	@Operation(description = "Lists examples") public void listExamples() {
		logger.debug("Listing examples");
		try {
			final ClassPath classPath = ClassPath.from(TestCommand.class.getClassLoader());
			final ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(EXAMPLES_PACKAGE);
			classes.forEach(klass -> writer.println(klass.getSimpleName()));
		} catch (final IOException e) {
			logger.error("Cannot load classes", e);
			writer.println("Error: Cannot load classes.");
		}
	}

	@Operation(description = "Executes a single example")
	@Parameter(name = "example", type = String.class, optional = false, description = "Execute a named example")
	public void executeExample(final Map<String, Object> parameters) {
		final String example = getAndCast(parameters, "example", String.class);

		logger.debug("Executing example");

		try {
			final WorkerConfiguration configuration = new SingleClassConfiguration(EXAMPLES_PACKAGE + '.' + example,
			                                                                       ImmutableList.of(System.getProperty(
				                                                                       "age.console.lib.path",
				                                                                       "lib/")));
			TimeUnit.SECONDS.sleep(1L);
			logger.debug("Sending {}", configuration);
			workerServiceClient.prepareConfiguration(configuration);
			TimeUnit.SECONDS.sleep(1L);
			workerServiceClient.startComputation();
		} catch (final InterruptedException e) {
			logger.debug("Interrupted", e);
		}
	}


	/**
	 * Operation to test interrupted computation.
	 *
	 * Currently it can run:
	 * # a computation stopped by cluster destruction,
	 * # a computation stopping because of its own error.
	 */
	@Operation(description = "Executes an interrupted computation")
	@Parameter(name = "type", type = String.class, optional = true, description = "Type of the computation")
	public void computationInterrupted(final Map<String, Object> parameters) {
		final String type = getAndCast(parameters, "type", String.class);

		logger.debug("Testing interrupted computation");

		writer.println("Loading class...");
		final String className = type.equals(Type.COMPUTE_ERROR.typeName())
		                         ? SimpleLongRunningWithError.class.getCanonicalName()
		                         : SimpleLongRunning.class.getCanonicalName();
		try {
			workerServiceClient.prepareConfiguration(new SingleClassConfiguration(className, ImmutableList.of(
				System.getProperty("age.console.lib.path", "lib/"))));
			writer.println("Starting computation...");
			workerServiceClient.startComputation();
			writer.println("Waiting...");

			TimeUnit.SECONDS.sleep(10L);
		} catch (final InterruptedException e) {
			logger.debug("Interrupted", e);
		}

		if (type.equals(Type.DESTROY.typeName())) {
			writer.println("Destroying cluster...");
			lifecycleServiceClient.destroyCluster();
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
