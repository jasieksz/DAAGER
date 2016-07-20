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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import pl.edu.agh.age.console.command.Command;
import pl.edu.agh.age.console.command.MainCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.google.common.collect.Sets;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Introspector that is able to provide information about available commands.
 */
@Named
public class CommandIntrospector {

	private static final Logger log = LoggerFactory.getLogger(CommandIntrospector.class);

	private final JCommander commander = new JCommander(new MainCommand());

	@Inject private Set<Command> commands;

	private @MonotonicNonNull Map<String, JCommander> commandsMap;

	@EnsuresNonNull("commandsMap") @PostConstruct private void construct() {
		log.debug("Injected commands: {}.", commands);
		commands.forEach(commander::addCommand);
		commandsMap = commander.getCommands();
	}

	public Set<String> allCommands() {
		return commandsMap.keySet();
	}

	public Set<String> commandsStartingWith(final String prefix) {
		requireNonNull(prefix);

		return commandsMap.keySet().stream().filter(s -> s.startsWith(prefix)).collect(toSet());
	}

	public Set<String> parametersOfCommand(final String command) {
		requireNonNull(command);
		checkArgument(!command.isEmpty());

		final Set<String> parameters = commandsMap.get(command)
		                                          .getParameters()
		                                          .stream()
		                                          .map(ParameterDescription::getLongestName)
		                                          .collect(toSet());
		final Command commandObject = (Command)getOnlyElement(commandsMap.get(command).getObjects());
		final Set<String> operations = commandObject.operations();
		return Sets.union(parameters, operations);
	}

	public Set<String> parametersOfCommandStartingWith(final String command, final String prefix) {
		requireNonNull(command);
		requireNonNull(prefix);
		checkArgument(!command.isEmpty());

		final Set<String> parameters = commandsMap.get(command)
		                                          .getParameters()
		                                          .stream()
		                                          .map(ParameterDescription::getLongestName)
		                                          .filter(s -> s.startsWith(prefix))
		                                          .collect(toSet());
		final Command commandObject = (Command)getOnlyElement(commandsMap.get(command).getObjects());
		final Set<String> operations = commandObject.operations()
		                                            .stream()
		                                            .filter(s -> s.startsWith(prefix))
		                                            .collect(toSet());
		return Sets.union(parameters, operations);
	}

	public Set<String> parametersOfMainCommand() {
		return commander.getParameters().stream().map(ParameterDescription::getLongestName).collect(toSet());
	}

	@Override public String toString() {
		return toStringHelper(this).add("commander", commander).toString();
	}
}