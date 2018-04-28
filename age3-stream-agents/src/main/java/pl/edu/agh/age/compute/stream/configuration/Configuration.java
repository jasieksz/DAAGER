/*
 * Copyright (C) 2016-2018 Intelligent Information Systems Group.
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

import pl.edu.agh.age.compute.api.topology.Topology;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.StopCondition;
import pl.edu.agh.age.compute.stream.logging.LoggingService;

import one.util.streamex.StreamEx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Configuration for Stream Agents computation.
 */
public final class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private final List<WorkplaceConfiguration<Agent>> workplaceConfigurations;

	private final StopCondition stopCondition;

	private final LoggingService loggingService;

	private final Topology<? extends Serializable> topology;

	public Configuration(final WorkplaceConfigurationGenerator<Agent> workplaceConfigurationGenerator,
	                     final StopCondition stopCondition, final LoggingService loggingService,
	                     final Topology<? extends Serializable> topology) {
		this.workplaceConfigurations = workplaceConfigurationGenerator.generateConfigurations();
		this.stopCondition = stopCondition;
		this.loggingService = loggingService;
		this.topology = topology;
	}

	public Configuration(final List<WorkplaceConfiguration<Agent>> workplaceConfigurations,
	                     final StopCondition stopCondition, final LoggingService loggingService,
	                     final Topology<? extends Serializable> topology) {
		this.workplaceConfigurations = new ArrayList<>(workplaceConfigurations);
		this.stopCondition = stopCondition;
		this.loggingService = loggingService;
		this.topology = topology;
	}

	public List<WorkplaceConfiguration<Agent>> workplaces() {
		final Optional<List<WorkplaceConfiguration<Agent>>> shared = StreamEx.cartesianPower(2, workplaceConfigurations)
		                                                                     .findAny(configs -> containsSameObjects(
			                                                                     configs.get(0), configs.get(1)));
		if (shared.isPresent()) {
			logger.warn(
				"Some of the configured workplaces share theirs objects with each other. "
				+ "It may result in degraded performance. "
				+ "Consider using `scope='prototype'` in the Spring configuration.");
		}
		return new ArrayList<>(workplaceConfigurations);
	}

	public StopCondition stopCondition() {
		return stopCondition;
	}

	public LoggingService loggingService() {
		return loggingService;
	}

	@SuppressWarnings("unchecked") public <T extends Serializable> Topology<T> topology() {
		return (Topology<T>)topology;
	}

	@SuppressWarnings("ObjectEquality")
	private static boolean containsSameObjects(final WorkplaceConfiguration<Agent> w1,
	                                           final WorkplaceConfiguration<Agent> w2) {
		final boolean different = w1 != w2;
		final boolean sameSteps = w1.step() == w2.step();
		final boolean sameAgents = w1.agents() == w2.agents();
		final boolean sameAfterSteps = w1.afterStep() == w2.afterStep();
		return different && (sameSteps || sameAgents || sameAfterSteps);
	}
}
