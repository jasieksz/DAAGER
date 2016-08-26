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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.console.command.Command;

import com.google.common.base.Throwables;

import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.completer.AggregateCompleter;
import org.jline.reader.impl.completer.completer.FileNameCompleter;
import org.jline.reader.impl.completer.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Console is the shell-like interface for managing the cluster and AgE nodes.
 *
 * A command is a class that implements the {@link Command} interface
 * and is annotated using the {@link javax.inject.Named} annotations.
 * Such commands are automatically recognized and available for the user.
 */
public final class Console {

	private static final String PROMPT = "AgE> ";

	private static final Pattern WHITESPACE = Pattern.compile("\\s");

	private static final Logger log = LoggerFactory.getLogger(Console.class);

	private final ApplicationContext applicationContext;

	private final CommandCompleter commandCompleter;

	private final Terminal terminal;

	private final PrintWriter writer;

	@Inject public Console(final ApplicationContext applicationContext, final CommandCompleter commandCompleter,
	                       final Terminal terminal) {
		this.applicationContext = requireNonNull(applicationContext);
		this.commandCompleter = requireNonNull(commandCompleter);
		this.terminal = requireNonNull(terminal);
		writer = terminal.writer();

		log.debug("Using {}", this.terminal);
	}

	void mainLoop() {
		final Completer completer = new AggregateCompleter(new FileNameCompleter(), new StringsCompleter("aaa"));
		final LineReader reader = LineReaderBuilder.builder()
		                                           .appName("AgE")
		                                           .terminal(terminal)
		                                           .completer(completer)
		                                           .build();

		writer.println("Welcome to the AgE console. Type help() to see usage information.");
		writer.flush();

		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.put("terminal", terminal);
		try {
			engine.eval("load('classpath:pl/edu/agh/age/console/base_commands.js');");
		} catch (final ScriptException e) {
			e.printStackTrace();
		}
		final Collection<Command> commands = applicationContext.getBeansOfType(Command.class).values();
		commands.forEach(command -> engine.put(command.name(), command));

		while (true) {
			try {
				final String line = reader.readLine(PROMPT);

				if (line == null) {
					continue;
				}
				log.debug("Read command: {}.", line);

				engine.eval(line);
			} catch (final UserInterruptException e) {
				log.debug("UIE", e);
			} catch (final EndOfFileException ignored) {
				return;
			} catch (final ScriptException e) {
				final Throwable rootCause = Throwables.getRootCause(e);
				writer.println("Parsing problem:");
				writer.println(rootCause.getMessage());
			}
		}
	}
}
