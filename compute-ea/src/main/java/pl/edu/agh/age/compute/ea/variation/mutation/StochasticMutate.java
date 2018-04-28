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

package pl.edu.agh.age.compute.ea.variation.mutation;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.rand.DoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.collection.Array;

/**
 * Abstract implementation of {@link IMutateSolution}. Features are not mutated independently. Instead, a random subset
 * of features is first selected (the size of this subset is proportional to the chance to mutate). Then these features
 * are mutated.
 *
 * Concrete subclasses are supposed to provide the actual mutation computation.
 *
 * @param <R>
 * 		the representation type of the solution to be mutated
 */
public abstract class StochasticMutate<T, S extends Solution<Array<T>>> implements Mutation<S> {

	private static final double DEFAULT_CHANCE_TO_MUTATE = 0.5;

	private final DoubleRandomGenerator doubleRand;

	private final IntRandomGenerator intRand;

	private final double chanceToMutate;

	protected StochasticMutate(final DoubleRandomGenerator doubleRand, final IntRandomGenerator intRand) {
		this(doubleRand, intRand, DEFAULT_CHANCE_TO_MUTATE);
	}

	protected StochasticMutate(final DoubleRandomGenerator doubleRand, final IntRandomGenerator intRand,
	                           final double chanceToMutate) {
		checkArgument(chanceToMutate >= 0.0, "Chance to mutate must be greater than 0");

		this.doubleRand = requireNonNull(doubleRand);
		this.intRand = requireNonNull(intRand);
		this.chanceToMutate = chanceToMutate;
	}

	@Override public final S mutate(final S solution) {
		Array<T> representation = solution.unwrap();
		final int size = representation.size();

		int mutatedBitsCount = (int)(chanceToMutate * size);
		final double chanceForExtraBit = chanceToMutate * size - mutatedBitsCount;
		final int extraBit = (doubleRand.nextDouble() < chanceForExtraBit) ? 1 : 0;
		mutatedBitsCount += extraBit;

		final boolean[] alreadyChecked = new boolean[size];
		for (int i = 0; i < mutatedBitsCount; i++) {
			int k = intRand.nextInt(size);
			while (alreadyChecked[k]) {
				k = intRand.nextInt(size);
			}
			alreadyChecked[k] = true;
			representation = doMutate(representation, k);
		}
		return (S)solution.cloneWithNewValue(representation);
	}

	/**
	 * Mutate the representation at the given index.
	 *
	 * This method purpose is to allow efficient unboxing in case of representations of primitives.
	 *
	 * @param representation
	 * 		the representation to be mutated
	 * @param index
	 * 		the index at which mutation should occur
	 *
	 * @return the new representation of the solution
	 */
	protected abstract Array<T> doMutate(Array<T> representation, int index);
}
