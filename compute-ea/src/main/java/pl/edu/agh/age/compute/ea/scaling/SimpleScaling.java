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


package pl.edu.agh.age.compute.ea.scaling;


import static com.google.common.base.Preconditions.checkArgument;

/**
 * Uniformly scales an array of values, so that they are positive, translated to 0 (regarding the smallest element),
 * normalized, and aggregated to 1.
 */
public final class SimpleScaling implements Scaling {

	@Override public double[] scale(final double[] data) {
		checkArgument(data.length != 0, "Data can't be empty");

		// Find minimum and sum
		double min = data[0];
		double sum = 0;
		for (final double value : data) {
			if (value < min) {
				min = value;
			}
			sum += value;
		}

		// Normalizes the data so that it sums up to 1 and then ... sum it up!

		// Same as substracting from all values and suming up again
		sum -= min * data.length;

		if (sum == 0) {
			// All elements are equal! Special case.
			data[0] = 1.0 / data.length;
			for (int i = 1; i < (data.length - 1); i++) {
				data[i] = Math.min(data[i - 1] + (1.0 / data.length), 1.0);
			}
		} else {
			data[0] = (data[0] - min) / sum;
			for (int i = 1; i < (data.length - 1); i++) {
				data[i] = Math.min(data[i - 1] + ((data[i] - min) / sum), 1.0);
			}
		}
		data[data.length - 1] = 1.0;

		return data;
	}
}
