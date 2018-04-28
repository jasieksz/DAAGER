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

package pl.edu.agh.age.compute.ea.variation.recombination;


import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.Function2;
import io.vavr.Tuple2;

/**
 * Recombination operator.
 *
 * @param <S>
 * 		the solution type.
 */
@FunctionalInterface
public interface Recombination<S extends Solution<?>> extends Function2<S, S, Tuple2<S, S>> {
	Tuple2<S, S> recombine(S firstSolution, S secondSolution);

	@Override default Tuple2<S, S> apply(final S s1, final S s2) {
		return recombine(s1, s2);
	}
}
