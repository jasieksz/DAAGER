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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.VectorSolution;

import java.io.Serializable;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;

/**
 * Recombination strategy that uniformly swaps two solutions representation with a probability of 0.5.
 *
 * @param <R>
 * 		the representation type of the solution to be recombined
 */
public class UniformRecombine<R extends Serializable> implements Recombination<VectorSolution<R>> {

	private final NormalizedDoubleRandomGenerator rand;

	public UniformRecombine(final NormalizedDoubleRandomGenerator rand) {
		this.rand = requireNonNull(rand);
	}

	@Override
	public final Tuple2<VectorSolution<R>, VectorSolution<R>> recombine(final VectorSolution<R> solution1,
	                                                                    final VectorSolution<R> solution2) {
		Array<R> representation1 = solution1.unwrap();
		Array<R> representation2 = solution2.unwrap();

		for (int i = 0, n = representation1.size(); i < n; i++) {
			if (rand.nextDouble() <= 0.5) {
				final Tuple2<Array<R>, Array<R>> tuple2 = swap(representation1, representation2, i);
				representation1 = tuple2._1;
				representation2 = tuple2._2;
			}
		}
		return Tuple.of(solution1.cloneWithNewValue(representation1), solution2.cloneWithNewValue(representation2));
	}

	/**
	 * Swaps the representations at the given index.
	 *
	 * This method purpose is to allow efficient unboxing in case of representations of primitives. Subclasses can then
	 * cast the given representation in the corresponding fastutil collection.
	 *
	 * @param representation1
	 * 		the first representation
	 * @param representation2
	 * 		the second representation
	 * @param index
	 * 		the index at which swapping should occur
	 */
	protected final Tuple2<Array<R>, Array<R>> swap(final Array<R> representation1, final Array<R> representation2,
	                                                final int index) {
		final R element = representation1.get(index);
		representation1.update(index, representation2.get(index));
		representation2.update(index, element);
		return Tuple.of(representation1, representation2);
	}
}
