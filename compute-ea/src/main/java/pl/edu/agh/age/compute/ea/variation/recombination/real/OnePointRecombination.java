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

package pl.edu.agh.age.compute.ea.variation.recombination.real;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.ea.variation.recombination.Recombination;

import io.vavr.Tuple;
import io.vavr.Tuple2;

/**
 * Recombines the representations of two real-valued solutions at a random point.
 */
public final class OnePointRecombination implements Recombination<DoubleVectorSolution> {

	private final IntRandomGenerator rand;

	public OnePointRecombination(final IntRandomGenerator rand) {
		this.rand = requireNonNull(rand);
	}

	@Override
	public Tuple2<DoubleVectorSolution, DoubleVectorSolution> recombine(final DoubleVectorSolution solution1,
	                                                                    final DoubleVectorSolution solution2) {
		final double[] representation1 = solution1.valuesAsPrimitive();
		final double[] representation2 = solution2.valuesAsPrimitive();
		checkArgument(representation1.length == representation2.length);

		final int recombinationPoint = rand.nextInt(representation1.length);

		final double[] tmp = new double[recombinationPoint];
		arraycopy(representation1, 0, tmp, 0, recombinationPoint);
		arraycopy(representation2, 0, representation1, 0, recombinationPoint);
		arraycopy(tmp, 0, representation2, 0, recombinationPoint);

		return Tuple.of(new DoubleVectorSolution(representation1), new DoubleVectorSolution(representation2));
	}
}
