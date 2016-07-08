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

import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeIdentityService;

import com.beust.jcommander.Parameters;

import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Named;

/**
 * Command for getting info about the local node.
 */
@Named
@Scope("prototype")
@Parameters(commandNames = "local", commandDescription = "Local node management", optionPrefixes = "--")
public final class LocalCommand extends BaseCommand {

	private enum Operation {
		INFO("info");

		private final String operationName;

		Operation(final String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(LocalCommand.class);

	/*@Inject*/ private NodeIdentityService identityService;

	public LocalCommand() {
		addHandler(Operation.INFO.operationName(), this::info);
	}

	@Override public final Set<String> operations() {
		return Arrays.stream(Operation.values()).map(Operation::operationName).collect(Collectors.toSet());
	}

	private void info(final Terminal printWriter) {
		final NodeDescriptor identity = identityService.descriptor();
		printWriter.writer().println("Local node info = {");
		printWriter.writer().println("\tid = " + identity.id());
		printWriter.writer().println("\ttype = " + identity.type());
		printWriter.writer().println("\tservices = " + identity.services());
		printWriter.writer().println("}");
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
