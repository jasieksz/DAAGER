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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.AfterStepAction;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.BeforeStepAction;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.emas.PopulationGenerator;

import java.util.List;
import java.util.stream.Collectors;

import io.vavr.collection.Stream;

public final class WorkplaceConfigurationGenerator<T extends Agent> {

	private final int workplacesCount;

	private final PopulationGenerator<T> generator;

	private final BeforeStepAction<T> beforeStep;

	private final Step<T> step;

	private final AfterStepAction<T, ?> afterStep;

	public WorkplaceConfigurationGenerator(final int workplacesCount, final PopulationGenerator<T> generator,
	                                       final Step<T> step, final AfterStepAction<T, ?> afterStep,
	                                       final BeforeStepAction<T> beforeStep) {
		checkArgument(workplacesCount > 0, "Workplaces count must be grater than zero");
		this.workplacesCount = workplacesCount;
		this.generator = requireNonNull(generator);
		this.beforeStep = requireNonNull(beforeStep);
		this.step = requireNonNull(step);
		this.afterStep = requireNonNull(afterStep);
	}

	public List<WorkplaceConfiguration<T>> generateConfigurations() {
		return Stream.range(0, workplacesCount).map(i -> createConfiguration()).collect(Collectors.toList());
	}

	private WorkplaceConfiguration<T> createConfiguration() {
		return new WorkplaceConfiguration<>(generator.createPopulation(), step, afterStep, beforeStep);
	}

}
