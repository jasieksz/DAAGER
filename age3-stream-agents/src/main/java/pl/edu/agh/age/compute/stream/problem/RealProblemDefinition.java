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

package pl.edu.agh.age.compute.stream.problem;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;

public final class RealProblemDefinition implements ProblemDefinition {

	private final int dimension;

	private final double[] lowerBounds;

	private final double[] upperBounds;

	public RealProblemDefinition(final int dimension, final double[] lowerBounds, final double[] upperBounds) {
		checkArgument(dimension > 0);
		checkArgument(lowerBounds.length == dimension);
		checkArgument(upperBounds.length == dimension);
		this.dimension = dimension;
		this.lowerBounds = lowerBounds.clone();
		this.upperBounds = upperBounds.clone();
	}

	public int dimension() {
		return dimension;
	}

	public double[] lowerBounds() {
		return lowerBounds.clone();
	}

	public double[] upperBounds() {
		return upperBounds.clone();
	}

	@Override public String representation() {
		return String.format("RealProblemDefinition of a dimension = %d", dimension);
	}

	public static RealProblemDefinition createSquareProblem(final int dimension, final double lowerBound,
	                                                        final double upperBound) {
		checkArgument(dimension > 0);
		checkArgument(lowerBound < upperBound);

		final double[] lowerBounds = new double[dimension];
		Arrays.fill(lowerBounds, lowerBound);

		final double[] upperBounds = new double[dimension];
		Arrays.fill(upperBounds, upperBound);

		return new RealProblemDefinition(dimension, lowerBounds, upperBounds);
	}

}
