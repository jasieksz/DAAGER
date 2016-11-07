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
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.console.command.Command.getAndCastDefault;
import static pl.edu.agh.age.console.command.Command.getAndCastNullable;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.worker.internal.configuration.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.configuration.SpringConfiguration;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

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

	private final ResourceLoader resourceLoader;

	@Inject
	public ComputationCommand(final WorkerServiceClient workerServiceClient,
	                          final DiscoveryServiceClient discoveryService, final Terminal terminal,
	                          final ResourceLoader resourceLoader) {
		this.workerServiceClient = requireNonNull(workerServiceClient);
		this.discoveryService = requireNonNull(discoveryService);
		this.resourceLoader = requireNonNull(resourceLoader);
		writer = terminal.writer();
	}

	@Override public String name() {
		return "computation";
	}

	@Operation(description = "Loads the configuration. One of the `class` and `config` is required.")
	@Parameter(name = "class", type = String.class, optional = true, description = "Fully-qualified class name to load")
	@Parameter(name = "config", type = String.class, optional = true, description = "Spring configuration file to load")
	@Parameter(name = "properties",
	           type = Map.class,
	           optional = true,
	           description = "Java properties to use with the provided configuration file as a Map<String, Object>")
	public void load(final Map<String, Object> parameters) {
		final Optional<String> classToLoad = getAndCastNullable(parameters, "class", String.class);
		final Optional<String> configToLoad = getAndCastNullable(parameters, "config", String.class);
		final Map<String, Object> properties = getAndCastDefault(parameters, "properties", Map.class, emptyMap());

		final WorkerConfiguration configuration;
		if (classToLoad.isPresent()) {
			logger.debug("Loading class {}", classToLoad);
			configuration = new SingleClassConfiguration(classToLoad.get());
		} else if (configToLoad.isPresent()) {
			logger.debug("Loading config from {}", configToLoad);
			final Resource resource = resourceLoader.getResource(configToLoad.get());
			if (resource.exists() && resource.isReadable()) {
				try {
					configuration = new SpringConfiguration(resource, properties);
				} catch (final IOException e) {
					writer.printf("File %s cannot be loaded due to an exception: %s.%n", configToLoad, e.getMessage());
					return;
				}
			} else {
				writer.printf("File %s cannot be loaded.%n", configToLoad);
				return;
			}
		} else {
			writer.println("You need to provide `config` or `class`.");
			return;
		}

		try {
			workerServiceClient.prepareConfiguration(configuration);
		} catch (final InterruptedException e) {
			logger.debug("Interrupted", e);
		}
	}

	@Operation(description = "Prints info about the computation") public void info() {
		final Set<NodeDescriptor> neighbours = discoveryService.allMembers();
		neighbours.forEach(writer::println);
	}

	@Operation(description = "Starts the computation") public void start() {
		workerServiceClient.startComputation();
	}

	@Operation(description = "Stops the computation") public void stop() {
		workerServiceClient.stopComputation();
	}

	@Operation(description = "Clean the configuration") public void clean() {
		workerServiceClient.cleanConfiguration();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}

