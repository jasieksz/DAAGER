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

import pl.edu.agh.age.compute.ea.AfterStepAction;
import pl.edu.agh.age.compute.ea.BeforeStepAction;
import pl.edu.agh.age.compute.ea.Manager;
import pl.edu.agh.age.compute.ea.Step;
import pl.edu.agh.age.compute.ea.Workplace;
import pl.edu.agh.age.compute.ea.solution.Solution;

import com.google.common.base.MoreObjects;

import io.vavr.collection.List;

/**
 * Configuration of a single workplace.
 *
 * @param <T>
 * 		type of agents in the workplace.
 */
public final class WorkplaceConfiguration<T extends Solution<?>> {

	private final Step<T> step;

	private final List<T> population;

	private final AfterStepAction<T, ?> afterStep;

	private final BeforeStepAction<T> beforeStep;

	public WorkplaceConfiguration(final List<T> population, final Step<T> step, final AfterStepAction<T, ?> afterStep) {
		this(population, step, afterStep, BeforeStepAction.simplePassthrough());
	}

	public WorkplaceConfiguration(final List<T> population, final Step<T> step, final AfterStepAction<T, ?> afterStep,
	                              final BeforeStepAction<T> beforeStep) {
		this.step = requireNonNull(step);
		this.population = requireNonNull(population);
		this.afterStep = requireNonNull(afterStep);
		this.beforeStep = requireNonNull(beforeStep);
	}

	@SuppressWarnings("unchecked") public Step<T> step() {
		return step;
	}

	public List<T> population() {
		return population;
	}

	public AfterStepAction<T, ?> afterStep() {
		return afterStep;
	}

	public Workplace<T> toWorkplace(final long id, final Manager manager) {
		return new Workplace<>(id, population, beforeStep, step, afterStep, manager);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this) //
		                  .add("step", step) //
		                  .add("population", population) //
		                  .add("afterStep", afterStep) //
		                  .add("beforeStep", beforeStep) //
		                  .toString();
	}
}
