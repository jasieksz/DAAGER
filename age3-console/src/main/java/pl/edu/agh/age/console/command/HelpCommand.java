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
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jdk.nashorn.internal.runtime.Undefined;

/**
 * Command for showing the help.
 *
 * This command is internal and is wrapped in base_commands.js
 */
@Named
public final class HelpCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(HelpCommand.class);

	private static final String HELP = "/pl/edu/agh/age/console/general_help.txt";

	private static final AttributedStyle BOLD_RED = AttributedStyle.BOLD.foreground(AttributedStyle.RED);

	private final PrintWriter writer;

	private final Collection<Command> commands;

	private final List<AttributedString> generalHelp;

	private final Terminal terminal;

	@Inject public HelpCommand(final Terminal terminal, final ApplicationContext applicationContext) {
		this.terminal = terminal;
		writer = new PrintWriter(terminal.writer(), true);
		commands = applicationContext.getBeansOfType(Command.class)
		                             .values()
		                             .stream()
		                             .filter(command -> !command.name().startsWith("_"))
		                             .collect(toList());

		List<AttributedString> lines; // Workaround for the compiler oddness ("variable already assigned")
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(HELP)))) {
			lines = reader.lines().map(l -> {
				final AttributedStringBuilder builder = new AttributedStringBuilder();
				builder.appendAnsi(l);
				return builder.toAttributedString();
			}).collect(toList());
		} catch (final IOException e) {
			lines = Collections.emptyList();
			writer.println("Cannot load help :(");
			logger.error("Cannot load help from {}", HELP, e);
		}
		generalHelp = lines;
	}

	@Override public String name() {
		return "_help";
	}

	@SuppressWarnings("unused") public void execute(final Object obj) {
		if ((obj == null) || (obj instanceof Undefined)) {
			generalHelp.stream().map(l -> l.toAnsi(terminal)).forEach(writer::println);
			commands.forEach(this::printCommandHelp);
		} else if (obj instanceof Command) {
			printCommandHelp((Command)obj);
		} else {
			writer.println("I have no help for this.");
		}
	}

	@SuppressWarnings("ObjectAllocationInLoop") private void printCommandHelp(final Command command) {
		final AttributedString commandHeader = new AttributedString(command.name(), BOLD_RED);
		writer.println(commandHeader.toAnsi(terminal));
		writer.println();

		final List<Method> methods = Arrays.stream(command.getClass().getMethods())
		                                   .filter(m -> m.isAnnotationPresent(Operation.class))
		                                   .collect(toList());
		for (final Method method : methods) {
			final Operation annotation = method.getAnnotation(Operation.class);

			final AttributedString methodHeader = new AttributedString(method.getName(), AttributedStyle.BOLD);
			final AttributedString methodDescription = new AttributedString(format("\t%s", annotation.description()));
			writer.println(methodHeader.toAnsi(terminal));
			writer.println(methodDescription);

			if (method.isAnnotationPresent(Parameters.class)) {
				final Parameter[] parameters = method.getAnnotation(Parameters.class).value();
				for (final Parameter parameter : parameters) {
					final String str = parameter.optional() ? "\t[%s] - %s" : "\t%s - %s";
					final AttributedString p = new AttributedString(format(str, parameter.name(),
					                                                       parameter.description()));
					writer.println(p.toAnsi(terminal));
				}
			}
			if (method.isAnnotationPresent(Parameter.class)) {
				final Parameter parameter = method.getAnnotation(Parameter.class);
				final String str = parameter.optional() ? "\t[%s] - %s" : "\t%s - %s";
				final AttributedString p = new AttributedString(format(str, parameter.name(), parameter.description()));
				writer.println(p.toAnsi(terminal));
			}
		}
		writer.println();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

}
