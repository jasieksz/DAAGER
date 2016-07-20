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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.AfterStepAction;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.BeforeStepAction;
import pl.edu.agh.age.compute.stream.Manager;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.Workplace;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Configuration of a single workplace.
 *
 * @param <T>
 * 		type of agents in the workplace.
 */
public final class WorkplaceConfiguration<T extends Agent> {

	private final Step<T> step;

	private final List<T> agents;

	private final AfterStepAction<T, ?> afterStep;

	private final BeforeStepAction<T> beforeStepAction;

	@Inject
	public WorkplaceConfiguration(final List<T> agents, final Step<T> step, final AfterStepAction<T, ?> afterStep) {
		this(agents, step, afterStep, BeforeStepAction.simpleMerge());
	}

	public WorkplaceConfiguration(final List<T> agents, final Step<T> step, final AfterStepAction<T, ?> afterStep,
	                              final BeforeStepAction<T> beforeStepAction) {
		this.step = requireNonNull(step);
		this.agents = requireNonNull(agents);
		this.afterStep = requireNonNull(afterStep);
		this.beforeStepAction = requireNonNull(beforeStepAction);
	}

	public Step<Agent> step() {
		return (Step<Agent>)step;
	}

	public List<Agent> agents() {
		return new ArrayList<>(agents);
	}

	public AfterStepAction<T, ?> afterStep() {
		return afterStep;
	}

	public Workplace<T> toWorkplace(final long id, final Manager manager) {
		return new Workplace<>(id, agents, beforeStepAction, step, afterStep, manager);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
		                  .add("step", step)
		                  .add("agents", agents)
		                  .add("afterStep", afterStep)
		                  .add("beforeStepAction", beforeStepAction)
		                  .toString();
	}
}
