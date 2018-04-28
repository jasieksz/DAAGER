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

import com.google.common.base.MoreObjects;

import one.util.streamex.StreamEx;

import org.apache.commons.lang3.ArrayUtils;

import io.vavr.collection.Array;

/**
 * Solution optimized for a vector of booleans.
 */
public final class BooleanVectorSolution extends VectorSolution<Boolean> {

	private static final long serialVersionUID = 6732761520984749613L;

	private final boolean[] primitives;

	public BooleanVectorSolution(final Boolean[] values, final double evaluation) {
		super(values, evaluation);
		primitives = ArrayUtils.toPrimitive(values);
	}

	public BooleanVectorSolution(final boolean[] values, final double evaluation) {
		super(ArrayUtils.toObject(values), evaluation);
		primitives = values.clone();
	}

	public BooleanVectorSolution(final boolean[] values) {
		this(values, Double.NaN);
	}

	public boolean[] valuesAsPrimitive() {
		return primitives.clone();
	}

	@Override public BooleanVectorSolution withEvaluation(final double evaluation) {
		return new BooleanVectorSolution(primitives, evaluation);
	}

	@Override public BooleanVectorSolution cloneWithNewValue(final Array<Boolean> newValues) {
		return new BooleanVectorSolution(ArrayUtils.toPrimitive(newValues.toJavaArray(Boolean.class)), evaluation);
	}

	@Override public String toString() {
		final String s = StreamEx.of(values()).map(b -> b ? '1' : '0').joining();
		return MoreObjects.toStringHelper(this).addValue(s).toString();
	}
}
