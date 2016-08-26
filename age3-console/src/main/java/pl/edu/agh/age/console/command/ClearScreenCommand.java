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

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command providing an action of clearing the screen.
 */
@Named
public final class ClearScreenCommand implements Command {

	private final Terminal terminal;

	@Inject public ClearScreenCommand(final Terminal terminal) {this.terminal = terminal;}

	@Override public String name() {
		return "clear";
	}

	public void execute() {
		terminal.puts(InfoCmp.Capability.clear_screen);
		terminal.flush();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
