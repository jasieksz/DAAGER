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

package pl.edu.agh.age.compute.stream;

import javaslang.Function2;
import javaslang.collection.List;

/**
 * Interface for functions executed before the step execution in a workplace.
 *
 * @param <T>
 * 		type of agents.
 */
@FunctionalInterface
public interface BeforeStepAction<T extends Agent> extends Function2<List<T>, List<T>, List<T>> {

	/**
	 * Simple merge action - adds all incoming agents to the population.
	 *
	 * @param <T>
	 * 		agents type.
	 *
	 * @return new population
	 */
	static <T extends Agent> BeforeStepAction<T> simpleMerge() {
		return List::appendAll;
	}
}
