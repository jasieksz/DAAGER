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
 * A non parallel problem that is bounded by the following domain:
 *
 * <pre>
 * [min[0], max[0]] x [min[1], max[1]] x ... x [min[dimension-1], max[dimension-1]]
 * </pre>
 *
 * @param <R>
 * 		The type of the problem bounds
 */
public class NonParallelProblem<R> implements VectorProblem<R> {

	private final int dimension;

	private final R[] min;

	private final R[] max;

	/**
	 * Creates a NonParallelProblem with the given upper and lower bounds.
	 *
	 * @param min
	 * 		the lower bounds
	 * @param max
	 * 		the upper bounds
	 */
	public NonParallelProblem(final R[] min, final R[] max) {
		checkArgument(min.length == max.length, "Min and max arrays must have same lenght");

		this.min = min.clone();
		this.max = max.clone();
		dimension = min.length;
	}

	@Override public final int dimension() {
		return dimension;
	}

	@Override public final R lowerBound(final int atDimension) {
		checkDimension(atDimension);
		return min[atDimension];
	}

	@Override public final R upperBound(final int atDimension) {
		checkDimension(atDimension);
		return max[atDimension];
	}

	private void checkDimension(final int atDimension) {
		checkArgument((atDimension >= 0) && (atDimension < dimension), "Dimension out of range");
	}
}
