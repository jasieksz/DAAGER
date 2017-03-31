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
 * Type tags used by the {@link DefaultLoggingService}
 */
public final class Tags {

	public static final String PROBLEM_DESCRIPTION_TAG = "[PD]";

	public static final String WORKPLACE_ENTRY_HEADER_TAG = "[WH]";

	public static final String WORKPLACE_ENTRY_TAG = "[W]";

	public static final String TICK_SUMMARY_HEADER_TAG = "[SH]";

	public static final String TICK_SUMMARY_TAG = "[S]";

	public static final String BEST_SOLUTION_HEADER_TAG = "[BH]";

	public static final String BEST_SOLUTION_TAG = "[B]";

	public static final String PROPERTIES_OPENING_TAG = "<properties>";

	public static final String PROPERTIES_CLOSING_TAG = "</properties>";

	private Tags() {}
}
