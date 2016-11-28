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

package pl.edu.agh.age.compute.stream.logging;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.problem.ProblemDefinition;

import java.time.Duration;

public final class LoggingParameters {

	private final ProblemDefinition problemDefinition;

	private final Duration loggingInterval;

	/**
	 * Instantiates new logging parameters.
	 *
	 * @param problemDefinition
	 * 		the problem definition
	 * @param loggingInterval
	 * 		the logging interval in milliseconds. Logging will be enabled if and only if this value is greater than 0.
	 * 		Otherwise (negative value or 0) logging will be switched off.
	 */
	public LoggingParameters(final ProblemDefinition problemDefinition, final Duration loggingInterval) {
		this.problemDefinition = requireNonNull(problemDefinition);
		this.loggingInterval = requireNonNull(loggingInterval);
	}

	public ProblemDefinition problemDefinition() {
		return problemDefinition;
	}

	/**
	 * Gets the logging interval value.
	 *
	 * @return the logging interval in milliseconds or {@code null} if logging should be entirely disabled
	 */
	public Duration loggingInterval() {
		return loggingInterval;
	}

	@Override public String toString() {
		return toStringHelper(this).add("problemDefinition", problemDefinition)
		                           .add("loggingInterval", loggingInterval)
		                           .toString();
	}

}
