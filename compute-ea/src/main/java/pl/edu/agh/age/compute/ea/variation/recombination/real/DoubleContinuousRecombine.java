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

import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.ea.variation.recombination.ContinuousRecombine;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;

/**
 * Abstract class which efficiently unboxes Double.
 */
public abstract class DoubleContinuousRecombine extends ContinuousRecombine<Double, DoubleVectorSolution> {

	@Override
	protected Tuple2<Array<Double>, Array<Double>> recombine(final Array<Double> representation1,
	                                                         final Array<Double> representation2, final int index) {
		final Double oldValue1 = representation1.get(index);
		final Double oldValue2 = representation2.get(index);

		final Array<Double> newRepresentation1 = representation1.update(index, recombine(oldValue1, oldValue2));
		final Array<Double> newRepresentation2 = representation2.update(index, recombine(oldValue1, oldValue2));
		return Tuple.of(newRepresentation1, newRepresentation2);
	}

	/**
	 * Perform the actual recombination on primitive doubles.
	 *
	 * @param value1
	 * 		The first value
	 * @param value2
	 * 		The second value
	 *
	 * @return a recombinated value
	 */
	protected abstract double recombine(double value1, double value2);
}
