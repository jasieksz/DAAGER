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

import static java.util.Collections.emptySet;

import com.beust.jcommander.JCommander;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.Set;

/**
 * Interface for commands used by the pl.edu.agh.age.console.
 *
 * Each implementing class should be annotated using {@link com.beust.jcommander.Parameters} and {@link
 * com.beust.jcommander.Parameter} annotations.
 */
@FunctionalInterface
public interface Command {

	/**
	 * Main method of the command - called when the command is executed.
	 *
	 * @param commander
	 * 		Current (per command) {@link JCommander} instance.
	 * @param reader
	 * 		Current {@link LineReader}.
	 * @param terminal
	 * 		Current writer - command should use this writer for output.
	 */
	void execute(JCommander commander, LineReader reader, Terminal terminal) throws IOException;

	/**
	 * Returns set of suboperations (subcommands) of the command in the from of strings. By default returns an empty
	 * set.
	 *
	 * @return suboperations of the command.
	 */
	default Set<String> operations() {
		return emptySet();
	}
}
