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
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for showing the help.
 */
@Named
public final class HelpCommand implements Command {
	private final Terminal terminal;

	private final PrintWriter writer;

	@Inject public HelpCommand(final Terminal terminal) {
		this.terminal = requireNonNull(terminal);

		writer = terminal.writer();
	}

	@Override public String name() {
		return "_help";
	}

	public void execute(final Object obj) {
		requireNonNull(obj);
		if (obj instanceof Command) {
			final Command command = (Command)obj;
			writer.println(format("%s", command.name()));
			final List<Method> methods = Arrays.stream(obj.getClass().getMethods())
			                                   .filter(m -> m.isAnnotationPresent(Operation.class))
			                                   .collect(toList());
			for (final Method method : methods) {
				final Operation annotation = method.getAnnotation(Operation.class);
				writer.println(format("%s - %s", method.getName(), annotation.description()));

				if (method.isAnnotationPresent(Parameters.class)) {
					final Parameter[] parameters = method.getAnnotation(Parameters.class).value();
					for (final Parameter parameter : parameters) {
						final String format = parameter.optional() ? "[%s] - %s" : "%s - %s";
						writer.println(format('\t' + format, parameter.name(), parameter.description()));
					}
				}
			}
		} else {
			terminal.writer().println("I have no help for this.");
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
