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
import static pl.edu.agh.age.console.command.Command.getAndCast;
import static pl.edu.agh.age.console.command.Command.getAndCastDefault;

import pl.edu.agh.age.client.LoggingClient;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

@Named
public final class LoggingCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(LoggingCommand.class);

	private static final String DEFAULT_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{40} - %msg%n";

	private final PrintWriter writer;

	private final LoggingClient loggingClient;

	@Inject public LoggingCommand(final LoggingClient loggingClient, final Terminal terminal) {
		this.loggingClient = loggingClient;
		writer = new PrintWriter(terminal.writer(), true);
	}

	@Override public String name() {
		return "logging";
	}

	@Operation(description = "Prints available logs") public void available() {
		loggingClient.availableLogs().forEach(id -> writer.printf("\t%s (%d)%n", id, loggingClient.countFor(id)));
	}

	@Operation(description = "Prints logs for the specified node")
	@Parameter(name = "id", type = String.class, optional = false, description = "ID of the node")
	@Parameter(name = "pattern", type = String.class, optional = true, description = "Pattern to use")
	public void print(final Map<String, Object> parameters) {
		final String id = getAndCast(parameters, "id", String.class);
		final String pattern = getAndCastDefault(parameters, "pattern", String.class, DEFAULT_PATTERN);
		final List<ILoggingEvent> events = loggingClient.logFor(id);

		final PatternLayout layout = getLayout(pattern);
		events.forEach(e -> writer.printf("%s", layout.doLayout(e)));
		layout.stop();
	}


	@Operation(description = "Save logs for the specified node to a file")
	@Parameter(name = "id", type = String.class, optional = false, description = "ID of the node")
	@Parameter(name = "path",
	           type = String.class,
	           optional = false,
	           description = "Path to a file in which the log will be saved")
	@Parameter(name = "pattern", type = String.class, optional = true, description = "Pattern to use")
	public void save(final Map<String, Object> parameters) {
		final String id = getAndCast(parameters, "id", String.class);
		final Path path = Paths.get(getAndCast(parameters, "path", String.class));
		final String pattern = getAndCastDefault(parameters, "pattern", String.class, DEFAULT_PATTERN);
		final List<ILoggingEvent> events = loggingClient.logFor(id);

		final PatternLayout layout = getLayout(pattern);
		printToFile(path, events, layout);
		layout.stop();
	}

	@Operation(description = "Save logs for all nodes to the directory")
	@Parameter(name = "path", type = String.class, optional = false, description = "Path to a directory")
	@Parameter(name = "pattern", type = String.class, optional = true, description = "Pattern to use")
	public void saveAll(final Map<String, Object> parameters) {
		final Path path = Paths.get(getAndCast(parameters, "path", String.class));
		final String pattern = getAndCastDefault(parameters, "pattern", String.class, DEFAULT_PATTERN);
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
			} catch (final IOException e) {
				logger.error("Could not create directory {}", path, e);
			}
		}
		if (!Files.isDirectory(path)) {
			logger.error("Path {} is not a directory", path);
			writer.printf("Path %s is not a directory%n", path.toString());
			return;
		}

		final PatternLayout layout = getLayout(pattern);
		loggingClient.availableLogs()
		             .forEach(id -> printToFile(path.resolve(id + ".log"), loggingClient.logFor(id), layout));
		layout.stop();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	private PatternLayout getLayout(final String pattern) {
		final PatternLayout layout = new PatternLayout();
		layout.setPattern(pattern);
		layout.setContext(new LoggerContext());
		layout.start();
		return layout;
	}

	private void printToFile(final Path path, final Iterable<ILoggingEvent> events, final PatternLayout layout) {
		try (BufferedWriter fileWriter = Files.newBufferedWriter(path)) {
			events.forEach(e -> {
				try {
					fileWriter.write(layout.doLayout(e));
				} catch (final IOException e1) {
					throw new UncheckedIOException(e1);
				}
			});
		} catch (final UncheckedIOException e) {
			logger.error("An error occurred when writing to the file {}", path, e);
			writer.printf("An error occurred when writing to the file: %s%n", e.getCause().getMessage());
		} catch (final IOException e) {
			logger.error("An error occurred when writing to the file {}", path, e);
			writer.printf("An error occurred when writing to the file: %s%n", e.getMessage());
		}
	}
}

