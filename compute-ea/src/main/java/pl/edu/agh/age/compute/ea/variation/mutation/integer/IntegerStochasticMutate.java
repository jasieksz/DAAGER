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


package pl.edu.agh.age.compute.ea.variation.mutation.integer;


import pl.edu.agh.age.compute.ea.rand.DoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.IntegerVectorSolution;
import pl.edu.agh.age.compute.ea.variation.mutation.StochasticMutate;

import io.vavr.collection.Array;

/**
 * Abstract class which efficiently unboxes Integer.
 */
public abstract class IntegerStochasticMutate extends StochasticMutate<Integer, IntegerVectorSolution> {

	IntegerStochasticMutate(final DoubleRandomGenerator doubleRand, final IntRandomGenerator intRand) {
		super(doubleRand, intRand);
	}

	@Override protected final Array<Integer> doMutate(final Array<Integer> representation, final int index) {
		return representation.update(index, doMutate(representation.get(index)));
	}

	/**
	 * Perform the actual mutation on a primitive int.
	 *
	 * @param value
	 * 		The old value
	 *
	 * @return a mutated value
	 */
	protected abstract int doMutate(int value);
}
