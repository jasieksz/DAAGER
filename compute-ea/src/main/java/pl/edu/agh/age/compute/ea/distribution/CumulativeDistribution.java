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


package pl.edu.agh.age.compute.ea.distribution;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Arrays;

/**
 * A cumulative distribution is a sequence of monotonic numbers in the range (0,..,1]
 */
public final class CumulativeDistribution {

	private final double[] data;

	/**
	 * Creates a CumulativeDistribution, using the provided data as internal representation.
	 *
	 * The assumptions on this data are that:
	 * - It is monotonic: {@code data[i+1] >= data[i]}
	 * - It is included in the range (0,1]: {@code data[0] >= 0.0, data[data.length] == 1.0}
	 *
	 * These assumptions are not checked, for efficiency purposes, but failing to meet them might result in undefined
	 * behavior.
	 *
	 * @param data
	 * 		The distribution data, should be a monotonic range included in (0,1]
	 */
	public CumulativeDistribution(final double[] data) {
		this.data = data.clone();
	}

	/**
	 * Looks up this cumulative distribution for a given argument. This method returns the index of the first element
	 * which is equal or greater than the given argument.
	 *
	 * The assumption on this argument is that it is included in the distribution range, that is [0,1]. (It can be 0, as
	 * we are looking for greater or equal elements). This assumption is not checked, for efficiency purposes, but
	 * failing to meet it might result in undefined behavior.
	 *
	 * @param argument
	 * 		The argument, should be contained in the range [0,1].
	 *
	 * @return The index of the first element equal or greater than the argument.
	 */
	public int getValueFor(final double argument) {
		final int search = Arrays.binarySearch(data, argument);
		return (search >= 0) ? search : (-search - 1);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(Arrays.toString(data)).toString();
	}
}
