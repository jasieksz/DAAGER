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

import static java.util.Objects.isNull;

import pl.edu.agh.age.console.command.Command;
import pl.edu.agh.age.console.command.HelpCommand;
import pl.edu.agh.age.console.command.MainCommand;
import pl.edu.agh.age.console.command.QuitCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Iterables;

import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.completer.AggregateCompleter;
import org.jline.reader.impl.completer.completer.FileNameCompleter;
import org.jline.reader.impl.completer.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.ExecPty;
import org.jline.terminal.impl.PosixSysTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Console is the shell-like interface for managing the cluster and AgE nodes.
 *
 * A command is a class that implements the {@link pl.edu.agh.age.console.command.Command} interface
 * and is annotated using the {@link com.beust.jcommander.Parameters} and {@link javax.inject.Named} annotations.
 * Such commands are automatically recognized and available for the user.
 */
public final class Console {

	private static final String PROMPT = "AgE> ";

	private static final Pattern WHITESPACE = Pattern.compile("\\s");

	private static final Logger log = LoggerFactory.getLogger(Console.class);

	@Inject private ApplicationContext applicationContext;

	@Inject private CommandCompleter commandCompleter;

	private final Terminal terminal;

	public Console() throws IOException {
		terminal = new PosixSysTerminal("t", "xterm-256color", ExecPty.current(), "UTF-8", true);
		log.debug("Using {}", terminal);
	}

	@PostConstruct private void construct() {
		//reader.addCompleter(commandCompleter);
	}

	public void mainLoop() throws IOException {
		final Completer completer = new AggregateCompleter(new FileNameCompleter(), new StringsCompleter("aaa"));
		final LineReader reader = LineReaderBuilder.builder()
		                                           .appName("AgE")
		                                           .terminal(terminal)
		                                           .completer(completer)
		                                           .build();

		terminal.writer().println("Welcome to the AgE console. Type help to see usage information.");
		terminal.writer().flush();

		while (true) {
			try {
				final String line = reader.readLine(PROMPT);

				if (line == null) {
					continue;
				}
				log.debug("Read command: {}.", line);

				// We need to allocate new instances every time
				final MainCommand mainCommand = new MainCommand();
				final JCommander mainCommander = new JCommander(mainCommand);
				final Collection<Command> commands = applicationContext.getBeansOfType(Command.class).values();
				commands.forEach(mainCommander::addCommand);
				mainCommander.parse(reader.getParsedLine().words().toArray(new String[0]));
				final String parsedCommand = mainCommander.getParsedCommand();
				log.debug("Parsed command: {}", parsedCommand);

				final Command command;
				final JCommander commander;
				if (isNull(parsedCommand)) {
					commander = mainCommander;
					command = mainCommand;
				} else {
					commander = mainCommander.getCommands().get(parsedCommand);
					command = (Command)Iterables.getOnlyElement(commander.getObjects());
				}
				// Because of limitations of JCommander, we need to do this using instanceof
				if (command instanceof QuitCommand) {
					break;
				}
				if (command instanceof HelpCommand) {
					mainCommander.usage();
				}
				command.execute(commander, reader, terminal);
			} catch (final ParameterException e) {
				terminal.writer().println(e.getLocalizedMessage());
				terminal.flush();
			} catch (final UserInterruptException e) {
				log.debug("UIE", e);
			} catch (final EndOfFileException ignored) {
				return;
			}
		}
	}
}
