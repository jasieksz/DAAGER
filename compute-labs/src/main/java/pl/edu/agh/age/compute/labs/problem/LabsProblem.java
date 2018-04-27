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

package pl.edu.agh.age.compute.labs.problem;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.logging.DefaultLoggingService;
import pl.edu.agh.age.compute.stream.problem.ProblemDefinition;

public final class LabsProblem implements ProblemDefinition {

	public static final String RUN_LENGTH_DISPLAY_FORMAT = "runLengthFormat";

	public static final String INTEGER_DISPLAY_FORMAT = "integerArrayFormat";

	private final int problemSize; // L - sequence length

	/**
	 * Instantiates a new LABS problem.
	 *
	 * @param problemSize
	 * 		the sequence length L defining the problem size
	 */
	public LabsProblem(final int problemSize) {
		checkArgument(problemSize > 0);
		this.problemSize = problemSize;
	}

	public int problemSize() {
		return problemSize;
	}

	@Override public String representation() {
		final String outputFormat = LabsSolution.RUN_LENGTH_DISPLAY_FORMAT
		                            ? RUN_LENGTH_DISPLAY_FORMAT
		                            : INTEGER_DISPLAY_FORMAT;
		return String.join(DefaultLoggingService.DELIMITER, "LABS", Integer.toString(problemSize), outputFormat);
	}

	@Override public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LabsProblem)) {
			return false;
		}
		final LabsProblem that = (LabsProblem)o;
		return problemSize == that.problemSize;
	}

	@Override public int hashCode() {
		return problemSize;
	}

}
