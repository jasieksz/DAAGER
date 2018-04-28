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

package pl.edu.agh.age.compute.ea.evaluation;

import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.Function1;

/**
 * Evaluator is a function that computes fitness of the solution.
 *
 * @param <S>
 * 		the type of the solution
 */
@FunctionalInterface
public interface Evaluator<S extends Solution<?>> extends Function1<S, Double> {
	double evaluate(S solution);

	@Override default Double apply(final S solution) {
		return evaluate(solution);
	}
}
