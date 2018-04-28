/*
 * Copyright (C) 2016-2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.ea;

import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.Function2;
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
 * @param <S>
 * 		type of a solution.
 */
@FunctionalInterface
public interface BeforeStepAction<S extends Solution<?>> extends Function2<Long, List<S>, List<S>> {

	/**
	 * Simple passthrough action – just passes the population.
	 *
	 * @param <T>
	 * 		agents type
	 *
	 * @return new population
	 */
	static <T extends Solution<?>> BeforeStepAction<T> simplePassthrough() {
		return (step, population) -> population;
	}
}
