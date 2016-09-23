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

import org.apache.commons.lang3.SystemUtils;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Terminal wrapper for easier Spring configuration.
 */
public final class TerminalBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TerminalBuilder.class);

	private TerminalBuilder() {}

	public static Terminal build() throws IOException {
		logger.debug("Executing our terminal builder");
		if (SystemUtils.IS_OS_WINDOWS) {
			// Seems that JNA does not work?
			return org.jline.terminal.TerminalBuilder.builder().jna(false).build();
		} else {
			return org.jline.terminal.TerminalBuilder.builder().jna(true).build();
		}
	}
}
