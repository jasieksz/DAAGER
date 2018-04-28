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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Arrays;

import io.vavr.collection.Array;

/**
 * Solution optimized for a vector of elements.
 */
public class VectorSolution<T extends Serializable> implements Solution<Array<T>> {

	private static final long serialVersionUID = 6732761520984749613L;

	protected final T[] values;

	protected final double evaluation;

	public VectorSolution(final T[] values, final double evaluation) {
		checkArgument(values.length > 0, "Size of an array must be greater than 0");
		this.values = values.clone();
		this.evaluation = evaluation;
	}

	public VectorSolution(final T[] values) {
		this(values, Double.NaN);
	}

	public final T[] values() {
		return values.clone();
	}

	public final int length() {
		return values.length;
	}

	@Override public final double evaluationValue() {
		return evaluation;
	}

	@Override public VectorSolution<T> withEvaluation(final double evaluation) {
		return new VectorSolution<>(values, evaluation);
	}

	@Override public final Array<T> unwrap() {
		return Array.of(values);
	}

	@Override public VectorSolution<T> cloneWithNewValue(final Array<T> newValues) {
		return new VectorSolution<>((T[])newValues.toJavaArray(), evaluation);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this).addValue(Arrays.toString(values)).toString();
	}
}
