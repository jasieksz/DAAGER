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

package pl.edu.agh.age.console;

import static com.google.common.base.Throwables.getRootCause;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.console.command.Command;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Console is the shell-like interface for managing the cluster and AgE nodes.
 *
 * A command is a class that implements the {@link Command} interface and is annotated using the {@link Named}
 * annotations. Such commands are automatically recognized and made available for the user.
 */
@Named
public final class Console {

	private static final Logger logger = LoggerFactory.getLogger(Console.class);

	private static final String BASE_SCRIPT = "classpath:pl/edu/agh/age/console/base_commands.js";

	private static final String PROMPT = "AgE> ";

	private final ApplicationContext applicationContext;

	private final Terminal terminal;

	private final PrintWriter writer;

	@Inject public Console(final ApplicationContext applicationContext, final Terminal terminal) {
		this.applicationContext = requireNonNull(applicationContext);
		this.terminal = requireNonNull(terminal);
		writer = new PrintWriter(terminal.writer(), true); // Wrap to enable autoFlush
		logger.debug("{}", terminal.getAttributes());

		logger.debug("Using {}", this.terminal);
	}

	void mainLoop(final String... args) {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval("load('" + BASE_SCRIPT + "');");
		} catch (final ScriptException e) {
			logger.error("Could not load the base script", e);
			writer.println("Could not load the configuration, exiting:");
			writer.println(e.getMessage());
			return;
		}

		final Collection<Command> commands = applicationContext.getBeansOfType(Command.class).values();
		logger.debug("Loaded commands: {}", commands);
		commands.forEach(command -> engine.put(command.name(), command));

		try {
			if (args.length > 0) {
				batch(engine, args);
			} else {
				interactive(engine);
			}
		} finally {
			closeTerminal();
		}
	}

	private void batch(final ScriptEngine engine, final String... args) {
		logger.info("Batch mode with args {}", Arrays.toString(args));
		writer.println("AgE Batch mode");

		for (final String arg : args) {
			logger.info("Parsing {}", arg);
			writer.printf("Parsing %s%n", arg);

			try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(arg))) {
				engine.eval(bufferedReader);
			} catch (final UserInterruptException e) { // XXX: never seen that one?
				logger.debug("UserInterruptException", e);
			} catch (final ScriptException e) {
				final Throwable rootCause = getRootCause(e);
				writer.println("Parsing problem:");
				writer.println(rootCause.getMessage());
			} catch (final IOException e) {
				final Throwable rootCause = getRootCause(e);
				writer.println("IO problem:");
				writer.println(rootCause.getMessage());
			}
		}
	}

	private void interactive(final ScriptEngine engine) {
		final LineReader reader = LineReaderBuilder.builder().appName("AgE").terminal(terminal).build();

		writer.println("Welcome to the AgE console. Type help() to see usage information.");
		while (true) {
			try {
				final String line = reader.readLine(PROMPT);
				if (line == null) {
					continue;
				}
				logger.debug("Read command: {}", line);
				engine.eval(line);
			} catch (final EndOfFileException ignored) {
				return;

			} catch (final UserInterruptException e) { // XXX: never seen that one?
				logger.debug("UserInterruptException", e);
			} catch (final ScriptException e) {
				final Throwable rootCause = getRootCause(e);
				writer.println("Parsing problem:");
				writer.println(rootCause.getMessage());
			}
		}
	}

	private void closeTerminal() {
		try {
			terminal.close();
		} catch (final IOException e) {
			final Throwable rootCause = getRootCause(e);
			writer.println("IO problem:");
			writer.println(rootCause.getMessage());
		}
	}
}
