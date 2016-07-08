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

package pl.edu.agh.age.console.command;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.nonNull;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.worker.internal.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.SpringConfiguration;
import pl.edu.agh.age.services.worker.internal.WorkerConfiguration;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting info about and managing the computation.
 */
@Named
@Scope("prototype")
@Parameters(commandNames = "computation", commandDescription = "Computation management", optionPrefixes = "--")
public final class ComputationCommand extends BaseCommand {

	private enum Operation {
		LOAD("load"),
		INFO("info"),
		START("start"),
		STOP("stop");

		private final String operationName;

		Operation(final String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(ComputationCommand.class);

	@Inject private DiscoveryServiceClient discoveryService;

	@Inject private WorkerServiceClient workerServiceClient;

	@Parameter(names = "--class") private String classToLoad;

	@Parameter(names = "--config") private String configToLoad;

	@Parameter(names = "--classpath") private String classpathToLoad;

	public ComputationCommand() {
		addHandler(Operation.LOAD.operationName(), this::load);
		addHandler(Operation.INFO.operationName(), this::info);
		addHandler(Operation.START.operationName(), this::start);
		addHandler(Operation.STOP.operationName(), this::stop);
	}

	@Override public Set<String> operations() {
		return Arrays.stream(Operation.values()).map(Operation::operationName).collect(Collectors.toSet());
	}

	private void load(final Terminal printWriter) {
		final WorkerConfiguration configuration;
		if (nonNull(classToLoad)) {
			log.debug("Loading class {}.", classToLoad);
			configuration = new SingleClassConfiguration(classToLoad);
		} else if (nonNull(configToLoad)) {
			log.debug("Loading config from {}.", configToLoad);
			try {
				configuration = SpringConfiguration.fromFilesystem(configToLoad);
			} catch (final IOException e) {
				printWriter.writer().println("File " + configToLoad + " cannot be loaded: " + e.getMessage());
				return;
			}
		} else if (nonNull(classpathToLoad)) {
			log.debug("Loading config from {}.", classpathToLoad);
			try {
				configuration = SpringConfiguration.fromClasspath(classpathToLoad);
			} catch (final IOException e) {
				printWriter.writer().println("File " + classpathToLoad + " cannot be loaded: " + e.getMessage());
				return;
			}
		} else {
			printWriter.writer().println("No class or config to load.");
			return;
		}

		try {
			workerServiceClient.prepareConfiguration(configuration);
		} catch (final InterruptedException e) {
			log.debug("Interrupted.", e);
		}
	}

	private void info(final Terminal printWriter) {
		log.debug("Printing information about info.");
		final Set<NodeDescriptor> neighbours = discoveryService.allMembers();
		neighbours.forEach(x -> printWriter.writer().println(x));
	}

	private void start(final Terminal printWriter) {
		workerServiceClient.startComputation();
	}

	private void stop(final Terminal printWriter) {
		workerServiceClient.stopComputation();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
