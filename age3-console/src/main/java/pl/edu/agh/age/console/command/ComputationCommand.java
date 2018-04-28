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

package pl.edu.agh.age.console.command;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.console.command.Command.getAndCastDefault;
import static pl.edu.agh.age.console.command.Command.getAndCastNullable;

import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.worker.internal.ComputationState;
import pl.edu.agh.age.services.worker.internal.configuration.SingleClassConfiguration;
import pl.edu.agh.age.services.worker.internal.configuration.SpringConfiguration;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;

import com.google.common.base.Charsets;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting info about and managing the computation.
 */
@Named
public final class ComputationCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(ComputationCommand.class);

	private final WorkerServiceClient workerServiceClient;

	private final PrintWriter writer;

	private final ResourceLoader resourceLoader;

	private final Terminal terminal;

	@Inject
	public ComputationCommand(final WorkerServiceClient workerServiceClient, final Terminal terminal,
	                          final ResourceLoader resourceLoader) {
		this.workerServiceClient = requireNonNull(workerServiceClient);
		this.resourceLoader = requireNonNull(resourceLoader);
		this.terminal = terminal;
		writer = new PrintWriter(terminal.writer(), true);
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
	           description = "Java properties to use with the provided configuration file as a Map<String, String>")
	@Parameter(name = "propertiesFiles",
	           type = List.class,
	           optional = true,
	           description = "Files with properties to use")
	@Parameter(name = "jars",
	           type = List.class,
	           optional = true,
	           description = "Paths to jar files to load")
	public void load(final Map<String, Object> parameters) {
		final Optional<String> classToLoad = getAndCastNullable(parameters, "class", String.class);
		final Optional<String> configToLoad = getAndCastNullable(parameters, "config", String.class);
		final Map<String, String> passedProperties = getAndCastDefault(parameters, "properties", Map.class, emptyMap());
		final List<String> propertiesFiles = getAndCastDefault(parameters, "propertiesFiles", List.class, emptyList());
		final List<String> jars = getAndCastDefault(parameters, "jars", List.class, emptyList());

		final WorkerConfiguration configuration;
		if (classToLoad.isPresent()) {
			logger.debug("Loading class {}", classToLoad);
			configuration = new SingleClassConfiguration(classToLoad.get(), jars);
		} else if (configToLoad.isPresent()) {
			logger.debug("Loading config from {}", configToLoad);
			final Resource resource = resourceLoader.getResource(configToLoad.get());
			final Properties properties = new Properties();
			for (final String propertiesFile : propertiesFiles) {
				final Resource resource1 = resourceLoader.getResource(propertiesFile);
				logger.debug("Loading passedProperties from {}", resource1);
				try {
					properties.load(new InputStreamReader(resource1.getInputStream(), Charsets.UTF_8));
				} catch (final IOException e) {
					writer.printf("File %s cannot be loaded due to an exception: %s.%n", resource1, e.getMessage());
					return;
				}
			}
			passedProperties.forEach(properties::setProperty);

			if (resource.exists() && resource.isReadable()) {
				try {
					configuration = new SpringConfiguration(resource, properties, jars);
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
		final ComputationState computationState = workerServiceClient.computationState();
		final AttributedString computationStateString = new AttributedString(computationState.toString(),
		                                                                     AttributedStyle.BOLD);
		writer.printf("Computation state: %s%n", computationStateString.toAnsi(terminal));

		final Optional<WorkerConfiguration> workerConfiguration = workerServiceClient.currentConfiguration();
		workerConfiguration.ifPresent(c -> {
			final AttributedString configurationString = new AttributedString(c.toString(), AttributedStyle.BOLD);
			writer.printf("Configuration: %s%n", configurationString.toAnsi(terminal));
		});

		final Optional<Throwable> error = workerServiceClient.currentError();
		error.ifPresent(t -> {
			final AttributedString errorString = new AttributedString(t.getMessage(), AttributedStyle.BOLD.foreground(
				AttributedStyle.RED));
			writer.printf("Error: %s%n", errorString.toAnsi(terminal));
			t.printStackTrace(writer);
		});
	}

	@Operation(description = "Starts the computation") public void start() {
		workerServiceClient.startComputation();
	}

	@Operation(description = "Waits for the computation to finish (or fail)") public void waitUntilFinished() {
		try {
			workerServiceClient.waitForComputationEnd();
		} catch (final InterruptedException e) {
			logger.debug("Interrupted", e);
			writer.println("Waiting was interrupted");
		}
		final AttributedString finished = new AttributedString("Computation finished",
		                                                       AttributedStyle.BOLD.foreground(AttributedStyle.BLUE));
		writer.printf("%s%n", finished.toAnsi(terminal));
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

