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

package pl.edu.agh.age.compute.stream.emas.reproduction.improvement;

import pl.edu.agh.age.compute.stream.emas.solution.Solution;

/**
 * Improvement operator.
 *
 * @param <S>
 * 		the solution type
 */
@FunctionalInterface
public interface Improvement<S extends Solution<?>> {
	/**
	 * Improvement method.
	 *
	 * **IMPORTANT:** The input solution must be already evaluated at this point and the returned one **MUST** also be
	 * properly evaluated inside this method!
	 *
	 * @param solution
	 * 		the solution to improve (must be evaluated)
	 *
	 * @return the improved and properly evaluated solution
	 */
	S improve(S solution);
}
