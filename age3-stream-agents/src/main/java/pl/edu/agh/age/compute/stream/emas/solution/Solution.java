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

package pl.edu.agh.age.compute.stream.emas.solution;

import java.io.Serializable;

/**
 * Solution is a specific value from the search domain and its fitness
 *
 * @param <T>
 * 		type of the value (dependent on the problem)
 *
 * @implSpec Implementations should be immutable.
 */
public interface Solution<T> extends Serializable {
	/**
	 * Returns the fitness of this solution. If the fitness was not computed, the returned value will be `Double#NaN`.
	 */
	double fitnessValue();

	/**
	 * Updates the fitness of this solution.
	 *
	 * This function returns a new instance.
	 *
	 * @param fitness
	 * 		new fitness value
	 *
	 * @return a copy of this solution with new fitness
	 */
	Solution<T> withFitness(double fitness);

	/**
	 * Returns the real value of this solution.
	 */
	T unwrap();
}
