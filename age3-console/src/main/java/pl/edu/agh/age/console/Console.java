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

import java.io.PrintWriter;
import java.util.Collection;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Console is the shell-like interface for managing the cluster and AgE nodes.
 *
 * A command is a class that implements the {@link Command} interface and is annotated using the {@link
 * javax.inject.Named} annotations. Such commands are automatically recognized and made available for the user.
 */
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
		writer = terminal.writer();

		logger.debug("Using {}", this.terminal);
	}

	void mainLoop() {
		final LineReader reader = LineReaderBuilder.builder().appName("AgE").terminal(terminal).build();
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval("load('" + BASE_SCRIPT + "');");
		} catch (final ScriptException e) {
			logger.error("Could not load the base script", e);
			writer.println("Could not load the configuration, exiting:");
			writer.println(e.getMessage());
			return;
		}

		writer.println("Welcome to the AgE console. Type help() to see usage information.");
		writer.flush();

		final Collection<Command> commands = applicationContext.getBeansOfType(Command.class).values();
		commands.forEach(command -> engine.put(command.name(), command));

		while (true) {
			try {
				final String line = reader.readLine(PROMPT);
				if (line == null) {
					continue;
				}
				logger.debug("Read command: {}.", line);
				engine.eval(line);
			} catch (final UserInterruptException e) { // XXX: never seen that one?
				logger.debug("UserInterruptException", e);
			} catch (final EndOfFileException ignored) {
				return;
			} catch (final ScriptException e) {
				final Throwable rootCause = getRootCause(e);
				writer.println("Parsing problem:");
				writer.println(rootCause.getMessage());
			}
		}
	}
}
