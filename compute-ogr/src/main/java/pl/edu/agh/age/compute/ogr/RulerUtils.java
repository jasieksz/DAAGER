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

package pl.edu.agh.age.compute.ogr;

import pl.edu.agh.age.compute.ogr.solution.Ruler;

import java.util.ArrayList;
import java.util.List;

public final class RulerUtils {

	private RulerUtils() { }

	/**
	 * Checks if a given ruler is valid.
	 *
	 * @param ruler
	 * 		the ruler
	 *
	 * @return <code>true</code>, if ruler is valid (all measurable distances are distinct)
	 */
	public static boolean isValid(final Ruler ruler) {
		return calculateViolations(ruler) == 0;
	}

	/**
	 * Calculates the ruler overall violation. <br />
	 * <i>The violation v(d) of a distance d in a n-mark ruler is the number of times distance d appears between two
	 * marks in the ruler. (...) The overall violation v of a n-mark ruler is simply the sum of the violations of its
	 * distances.</i> [<a href="http://arantxa.ii.uam.es/~idotu/golomb.pdf">source</a>]
	 *
	 * @param ruler
	 * 		the ruler
	 *
	 * @return the overall violation of a ruler
	 */
	public static int calculateViolations(final Ruler ruler) {
		final int size = ruler.directRepresentation().length;
		final int[] distances = new int[ruler.length() + 1]; // distance occurrence matrix

		for (int i = 0; i < (size - 1); i++) {
			for (int j = i + 1; j < size; j++) {
				final int idx = ruler.directRepresentation()[j] - ruler.directRepresentation()[i];
				distances[idx]++;
			}
		}

		int violationCount = 0;
		for (final int distance : distances) {
			if (distance > 0) {
				violationCount += distance - 1; // each distance can occur only ONCE
			}
		}

		return violationCount;
	}

	/**
	 * Gets the measurable distances. Distances in the result list are ordered as they appear in the ruler.
	 *
	 * @param ruler
	 * 		the ruler
	 * @param includeOneLengthDistance
	 * 		the flag indicating whether or not to include a distance of one unit
	 *
	 * @return the measurable distances list
	 */
	public static List<Integer> getMeasurableDistances(final Ruler ruler, final boolean includeOneLengthDistance) {
		final int length = includeOneLengthDistance ? (ruler.marksCount() - 1) : (ruler.marksCount() - 2);
		final List<Integer> measurableDistances = new ArrayList<>(length);
		for (int i = 0; i < (ruler.marksCount() - 1); i++) {
			final int value = ruler.indirectRepresentation()[i];
			if ((value != 1) || ((value == 1) && includeOneLengthDistance)) {
				measurableDistances.add(value);
			}
		}
		return measurableDistances;
	}

}
