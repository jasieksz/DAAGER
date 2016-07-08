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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.inject.Named;

/**
 * Command providing an action of clearing the screen.
 */
@Named
@Parameters(commandNames = "clear", commandDescription = "Clear screen")
public final class ClearScreenCommand implements Command {

	private static final Logger log = LoggerFactory.getLogger(ClearScreenCommand.class);

	@Override public void execute(final JCommander commander, final LineReader reader, final Terminal terminal)
			throws IOException {
		log.debug("Clearing the screen.");
		terminal.puts(InfoCmp.Capability.clear_screen);
		terminal.flush();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
