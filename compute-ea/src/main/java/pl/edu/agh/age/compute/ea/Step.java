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

package pl.edu.agh.age.compute.ea;

import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.collection.List;

@FunctionalInterface
public interface Step<T extends Solution<?>> {

	/**
	 * Method invoked during each iteration. Make sure that a minimum number of operations is executed here.
	 *
	 * @param stepNumber
	 * 		the current step number
	 * @param population
	 * 		the current population
	 * @param environment
	 * 		the environment
	 *
	 * @return the new population - a result of step iteration calculations
	 */
	List<T> stepOn(long stepNumber, List<T> population);

}
