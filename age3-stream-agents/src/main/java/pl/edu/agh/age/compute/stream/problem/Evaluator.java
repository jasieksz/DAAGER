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

package pl.edu.agh.age.compute.stream.problem;

import pl.edu.agh.age.compute.stream.emas.solution.Solution;

/**
 * Evaluator is a function that computes fitness of the solution.
 *
 * @param <S>
 * 		type of the solution.
 * @param <R>
 * 		type of the results (fitness).
 */
@FunctionalInterface
public interface Evaluator<S extends Solution<?>, R> {
	R evaluate(S value);
}
