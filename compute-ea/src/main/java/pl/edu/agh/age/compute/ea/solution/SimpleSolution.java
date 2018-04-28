/*
 * Copyright (C) 2016-2016 Intelligent Information Systems Group.
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

import static com.google.common.base.MoreObjects.toStringHelper;

import java.io.Serializable;

/**
 * Basic implementation of the {@link Solution} interface. Stores any serializable object.
 */
public final class SimpleSolution<T extends Serializable> implements Solution<T> {

	private static final long serialVersionUID = -1330253069016370993L;

	private final T value;

	private final double evaluation;

	public SimpleSolution(final T value, final double evaluation) {
		this.value = value;
		this.evaluation = evaluation;
	}

	public SimpleSolution(final T value) {
		this(value, Double.NaN);
	}

	@Override public double evaluationValue() {
		return evaluation;
	}

	@Override public SimpleSolution<T> withEvaluation(final double evaluation) {
		return new SimpleSolution<>(value, evaluation);
	}

	@Override public T unwrap() {
		return value;
	}

	@Override public SimpleSolution<T> cloneWithNewValue(final T newValue) {
		return new SimpleSolution<>(newValue, evaluation);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(value).toString();
	}
}
