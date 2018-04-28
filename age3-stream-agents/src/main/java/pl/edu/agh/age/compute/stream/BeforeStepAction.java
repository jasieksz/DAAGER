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

package pl.edu.agh.age.compute.stream;

import io.vavr.Function3;
import io.vavr.collection.List;

/**
 * Interface for functions executed before the step execution in a workplace.
 *
 * The parameters of the function are:
 * - step number (as Long),
 * - current population (as {@link List}),
 * - incoming population - from migrations (as {@link List}),
 *
 * The function should return the population to process in the current step.
 *
 * @param <T>
 * 		type of agents.
 */
@FunctionalInterface
public interface BeforeStepAction<T extends Agent> extends Function3<Long, List<T>, List<T>, List<T>> {

	/**
	 * Simple merge action - adds all incoming agents to the population.
	 *
	 * @param <T>
	 * 		agents type
	 *
	 * @return new population
	 */
	static <T extends Agent> BeforeStepAction<T> simpleMerge() {
		return (step, population, newAgents) -> population.appendAll(newAgents);
	}
}
