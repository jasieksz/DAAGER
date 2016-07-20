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

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import pl.edu.agh.age.annotation.ForTestsOnly;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A class to simplify creation of commands with suboperations.
 */
public abstract class BaseCommand implements Command {

	private final Map<String, Consumer<Terminal>> handlers = newHashMap();

	@Parameter private final @MonotonicNonNull List<String> unnamed = newArrayList();

	protected final void addHandler(final String commandName, final Consumer<Terminal> handler) {
		handlers.put(commandName, handler);
	}

	@Override public void execute(final JCommander commander, final LineReader reader, final Terminal printWriter) {
		final String command = getOnlyElement(unnamed, "");
		if (!handlers.containsKey(command)) {
			printWriter.writer().println("Unknown command '" + command + "'.");
			return;
		}
		handlers.get(command).accept(printWriter);
	}

	@ForTestsOnly final void setUnnamed(final List<String> unnamed) {
		this.unnamed.addAll(unnamed);
	}
}