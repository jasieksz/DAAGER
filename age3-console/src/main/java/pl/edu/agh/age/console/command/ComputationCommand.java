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
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.console.command.Command.getAndCastNullable;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.worker.internal.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.SpringConfiguration;
import pl.edu.agh.age.services.worker.internal.WorkerConfiguration;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting info about and managing the computation.
 */
@Named
public final class ComputationCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(ComputationCommand.class);

	private final DiscoveryServiceClient discoveryService;

	private final WorkerServiceClient workerServiceClient;

	private final PrintWriter writer;

	@Inject
	public ComputationCommand(final WorkerServiceClient workerServiceClient,
	                          final DiscoveryServiceClient discoveryService, final Terminal terminal) {
		this.workerServiceClient = requireNonNull(workerServiceClient);
		this.discoveryService = requireNonNull(discoveryService);
		writer = terminal.writer();
	}

	@Override public String name() {
		return "computation";
	}

	@Operation(description = "Loads the configuration")
	@Parameter(name = "class", type = String.class, optional = true, description = "")
	@Parameter(name = "fs-config", type = String.class, optional = true, description = "")
	@Parameter(name = "cp-config", type = String.class, optional = true, description = "")
	public void load(final Map<String, Object> parameters) {
		final Optional<String> classToLoad = getAndCastNullable(parameters, "class", String.class);
		final Optional<String> fsConfig = getAndCastNullable(parameters, "fs-config", String.class);
		final Optional<String> cpConfig = getAndCastNullable(parameters, "cp-config", String.class);

		final WorkerConfiguration configuration;
		if (classToLoad.isPresent()) {
			logger.debug("Loading class {}.", classToLoad);
			configuration = new SingleClassConfiguration(classToLoad.get());
		} else if (fsConfig.isPresent()) {
			logger.debug("Loading config from {}.", fsConfig);
			try {
				configuration = SpringConfiguration.fromFilesystem(fsConfig.get());
			} catch (final IOException e) {
				writer.println("File " + fsConfig + " cannot be loaded: " + e.getMessage());
				return;
			}
		} else if (cpConfig.isPresent()) {
			logger.debug("Loading config from {}.", cpConfig);
			try {
				configuration = SpringConfiguration.fromClasspath(cpConfig.get());
			} catch (final IOException e) {
				writer.println("File " + cpConfig + " cannot be loaded: " + e.getMessage());
				return;
			}
		} else {
			writer.println("No class or config to load.");
			return;
		}

		try {
			workerServiceClient.prepareConfiguration(configuration);
		} catch (final InterruptedException e) {
			logger.debug("Interrupted.", e);
		}
	}

	@Operation(description = "Prints info about computation") public void info() {
		logger.debug("Printing information about info.");
		final Set<NodeDescriptor> neighbours = discoveryService.allMembers();
		neighbours.forEach(writer::println);
	}

	@Operation(description = "Starts the computation") public void start() {
		workerServiceClient.startComputation();
	}

	@Operation(description = "Stops the computation") public void stop() {
		workerServiceClient.stopComputation();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
