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

/**
 * Columns used by the {@link DefaultLoggingService}
 */
public final class Columns {

	public static final String TIME = "TIME";

	public static final String WORKPLACE_ID = "WORKPLACE_ID";

	public static final String BEST_SOLUTION_SO_FAR = "BEST_SOLUTION_SO_FAR";

	public static final String FITNESS_EVALUATIONS = "FITNESS_EVALUATIONS";

	public static final String SOLUTION_STRING = "SOLUTION_STRING";

	public static final String SOLUTION_WORKPLACE_ID = "SOLUTION_WORKPLACE_ID";

	public static final String SOLUTION_WORKPLACE_STEP_NUMBER = "SOLUTION_WORKPLACE_STEP_NUMBER";

	public static final String SOLUTION_OCURRANCE_COUNT = "SOLUTION_OCURRANCE_COUNT";

	static final String[] BEST_SOLUTION_HEADER = {Tags.BEST_SOLUTION_HEADER_TAG, //
	                                              SOLUTION_STRING, //
	                                              SOLUTION_WORKPLACE_ID, //
	                                              SOLUTION_WORKPLACE_STEP_NUMBER, //
	                                              SOLUTION_OCURRANCE_COUNT};

	static final String[] SUMMARY_HEADER = {Tags.TICK_SUMMARY_HEADER_TAG, //
	                                        TIME, //
	                                        BEST_SOLUTION_SO_FAR, //
	                                        FITNESS_EVALUATIONS};

	private Columns() {}
}
