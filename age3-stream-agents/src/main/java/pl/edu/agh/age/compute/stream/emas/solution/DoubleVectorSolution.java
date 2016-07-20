/*
 * Copyright (C) 2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.stream.emas.solution;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;

import java.util.Arrays;

import javaslang.collection.Array;

/**
 * Solution optimized for a vector of doubles.
 */
public final class DoubleVectorSolution implements Solution<Array<Double>> {

	private final double[] values;

	public DoubleVectorSolution(final double[] values) {
		checkArgument(values.length > 0, "Size of an array must be greater than 0");
		this.values = Arrays.copyOf(values, values.length);
	}

	public double[] values() {
		return Arrays.copyOf(values, values.length);
	}

	public int length() {
		return values.length;
	}

	@Override public Array<Double> unwrap() {
		return Array.ofAll(values);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this).addValue(Arrays.toString(values)).toString();
	}
}
