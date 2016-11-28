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

package pl.edu.agh.age.compute.stream.configuration;

import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.StopCondition;
import pl.edu.agh.age.compute.stream.logging.LoggingService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Configuration for Stream Agents computation.
 */
public final class Configuration {

	private final List<WorkplaceConfiguration<Agent>> workplaceConfigurations;

	private final StopCondition stopCondition;

	private final LoggingService loggingService;

	@Inject
	public Configuration(final List<WorkplaceConfiguration<Agent>> workplaceConfigurations,
	                     final StopCondition stopCondition, final LoggingService loggingService) {
		this.workplaceConfigurations = new ArrayList<>(workplaceConfigurations);
		this.stopCondition = stopCondition;
		this.loggingService = loggingService;
	}

	public List<WorkplaceConfiguration<Agent>> workplaces() {
		return new ArrayList<>(workplaceConfigurations);
	}

	public StopCondition stopCondition() {
		return stopCondition;
	}

	public LoggingService loggingService() {
		return loggingService;
	}
}
