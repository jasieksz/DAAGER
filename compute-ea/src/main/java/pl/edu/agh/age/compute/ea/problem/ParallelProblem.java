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


package pl.edu.agh.age.compute.ea.problem;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A parallel problem that is bounded by the following hypercube
 *
 * <pre>
 * [min, max] x [min, max] x ... x [min, max]
 * </pre>
 *
 * @param <R>
 * 		The type of the problem bounds
 */
public class ParallelProblem<R> implements VectorProblem<R> {

	private final int dimension;

	private final R min;

	private final R max;

	/**
	 * Creates a ParallelRoblem with a range of [min, max] and a given dimension.
	 *
	 * @param dimension
	 * 		This problem dimension
	 * @param min
	 * 		This problem lower bound
	 * @param max
	 * 		This problem upper bound
	 */
	public ParallelProblem(final int dimension, final R min, final R max) {
		checkArgument(dimension > 0, "Dimension must be greater than 0");

		this.dimension = dimension;
		this.min = min;
		this.max = max;
	}

	@Override public final int dimension() {
		return dimension;
	}

	@Override public final R lowerBound(final int atDimension) {
		checkDimension(atDimension);
		return min;
	}

	@Override public final R upperBound(final int atDimension) {
		checkDimension(atDimension);
		return max;
	}

	private void checkDimension(final int atDimension) {
		checkArgument((atDimension >= 0) && (atDimension < dimension), "Dimension out of range");
	}
}
