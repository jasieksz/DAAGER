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


package pl.edu.agh.age.compute.ea.variation.mutation.binary;

import pl.edu.agh.age.compute.ea.rand.DoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;
import pl.edu.agh.age.compute.ea.variation.mutation.StochasticMutate;

import io.vavr.collection.Array;

/**
 * Flips the given binary feature.
 */
public final class BinaryMutate extends StochasticMutate<Boolean, BooleanVectorSolution> {

	public BinaryMutate(final DoubleRandomGenerator doubleRand, final IntRandomGenerator intRand) {
		super(doubleRand, intRand);
	}

	@Override protected Array<Boolean> doMutate(final Array<Boolean> representation, final int index) {
		return representation.update(index, !representation.get(index));
	}
}
