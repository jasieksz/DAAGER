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

package pl.edu.agh.age.compute.ea.solution;

import org.apache.commons.lang3.ArrayUtils;

import io.vavr.collection.Array;

/**
 * Solution optimized for a vector of integers.
 */
public final class IntegerVectorSolution extends VectorSolution<Integer> {

	private static final long serialVersionUID = 6732761520984749613L;

	private final int[] primitives;

	public IntegerVectorSolution(final Integer[] values, final double evaluation) {
		super(values, evaluation);
		primitives = ArrayUtils.toPrimitive(values);
	}

	public IntegerVectorSolution(final int[] values, final double evaluation) {
		super(ArrayUtils.toObject(values), evaluation);
		primitives = values.clone();
	}

	public IntegerVectorSolution(final int[] values) {
		this(values, Double.NaN);
	}

	public int[] valuesAsPrimitive() {
		return primitives.clone();
	}

	@Override public IntegerVectorSolution withEvaluation(final double evaluation) {
		return new IntegerVectorSolution(primitives, evaluation);
	}

	@Override public IntegerVectorSolution cloneWithNewValue(final Array<Integer> newValues) {
		return new IntegerVectorSolution(ArrayUtils.toPrimitive(newValues.toJavaArray(Integer.class)), evaluation);
	}
}
