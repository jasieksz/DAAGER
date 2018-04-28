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

package pl.edu.agh.age.compute.ea.configuration;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.StopCondition;
import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.collection.List;

/**
 * Configuration for Stream Agents computation.
 */
public final class Configuration<T extends Solution<?>> {

	private final List<WorkplaceConfiguration<T>> workplaceConfigurations;

	private final StopCondition stopCondition;

	public Configuration(final List<WorkplaceConfiguration<T>> workplaceConfigurations,
	                     final StopCondition stopCondition) {
		this.workplaceConfigurations = requireNonNull(workplaceConfigurations);
		this.stopCondition = requireNonNull(stopCondition);
	}

	public Configuration(final java.util.List<WorkplaceConfiguration<T>> workplaceConfigurations,
	                     final StopCondition stopCondition) {
		this(List.ofAll(workplaceConfigurations), stopCondition);
	}

	public List<WorkplaceConfiguration<T>> workplaces() {
		return workplaceConfigurations;
	}

	public StopCondition stopCondition() {
		return stopCondition;
	}

}
