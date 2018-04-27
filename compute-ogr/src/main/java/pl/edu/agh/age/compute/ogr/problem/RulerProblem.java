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

package pl.edu.agh.age.compute.ogr.problem;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.ogr.solution.Ruler;
import pl.edu.agh.age.compute.stream.logging.DefaultLoggingService;
import pl.edu.agh.age.compute.stream.problem.ProblemDefinition;

import java.util.Arrays;

public final class RulerProblem implements ProblemDefinition {

	public static final String DIRECT_REPRESENTATION_FORMAT = "directRepresentationFormat";

	public static final String INDIRECT_REPRESENTATION_FORMAT = "indirectRepresentationFormat";

	private final int marksCount; // m - order

	private final int maxAllowedDistance;

	/**
	 * Instantiates a new OGR problem.
	 *
	 * @param marksCount
	 * 		the order of a ruler (number of marks on it)
	 * @param maxAllowedDistance
	 * 		the maximum allowed distance on a ruler
	 */
	public RulerProblem(final int marksCount, final int maxAllowedDistance) {
		checkArgument(marksCount > 0);
		checkArgument(maxAllowedDistance >= 0);
		this.marksCount = marksCount;
		this.maxAllowedDistance = maxAllowedDistance;
	}

	public int marksCount() {
		return marksCount;
	}

	public int maxAllowedDistance() {
		return maxAllowedDistance;
	}

	@Override public String representation() {
		final String outputFormat = Ruler.DIRECT_REPRESENTATION_FORMAT
		                            ? DIRECT_REPRESENTATION_FORMAT
		                            : INDIRECT_REPRESENTATION_FORMAT;
		return String.join(DefaultLoggingService.DELIMITER, "OGR", Integer.toString(marksCount),
		                   Integer.toString(maxAllowedDistance), outputFormat);
	}

	@Override public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RulerProblem)) {
			return false;
		}
		final RulerProblem that = (RulerProblem)o;
		return (marksCount == that.marksCount) && (maxAllowedDistance == that.maxAllowedDistance);
	}

	@Override public int hashCode() {
		return Arrays.hashCode(new int[] {marksCount, maxAllowedDistance});
	}

}
