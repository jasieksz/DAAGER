/*
 * Copyright (C) 2006-2018 Intelligent Information Systems Group.
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

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;

/**
 * Abstract implementation of a continuous recombination.The recombined value is located continuously between the
 * parents values.
 *
 * Concrete subclasses are supposed to provide the actual computation.
 *
 * @param <R>
 * 		the representation type of the solution to be recombined
 */
public abstract class ContinuousRecombine<R, S extends Solution<Array<R>>> implements Recombination<S> {

	@Override public final Tuple2<S, S> recombine(final S solution1, final S solution2) {
		Array<R> representation1 = solution1.unwrap();
		Array<R> representation2 = solution2.unwrap();

		for (int i = 0, n = representation1.size(); i < n; i++) {
			final Tuple2<Array<R>, Array<R>> tuple2 = recombine(representation1, representation2, i);
			representation1 = tuple2._1;
			representation2 = tuple2._2;
		}
		return Tuple.of((S)solution1.cloneWithNewValue(representation1),
		                (S)solution2.cloneWithNewValue(representation2));
	}

	/**
	 * Recombines the representations at the given index.
	 *
	 * This method purpose is to allow efficient unboxing in case of representations of primitives.
	 *
	 * @param representation1
	 * 		the first representation
	 * @param representation2
	 * 		the second representation
	 * @param index
	 * 		the index at which recombination should occur
	 */
	protected abstract Tuple2<Array<R>, Array<R>> recombine(Array<R> representation1, Array<R> representation2,
	                                                        int index);
}
