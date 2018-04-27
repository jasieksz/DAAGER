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

package pl.edu.agh.age.compute.labs.solution;

public final class LabsSolutionPrinter {

	private LabsSolutionPrinter() { }

	/**
	 * Returns a traditional string representation of a binary sequence, e.g. [ +1, +1, -1, +1, -1, -1]
	 *
	 * @param sequence
	 * 		the sequence
	 *
	 * @return the string representation of a sequence
	 */
	public static String exactStringRepresentation(final boolean[] sequence) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");

		for (final boolean element : sequence) {
			builder.append(" ").append(element ? "+1" : "-1");
		}

		builder.append(" ]");
		return builder.toString();
	}

	/**
	 * Returns a run length format representation of a binary sequence, e.g. [ + 2 1 1 2 ] for sequence [ +1, +1, -1,
	 * +1, -1, -1] or [ - 2 1 1 2 ] for [ -1, -1, +1, -1, +1, +1]. This notation is useful for comparing algorithm
	 * results with official results from the Internet.
	 *
	 * @param sequence
	 * 		the sequence
	 *
	 * @return the run-length string representation of a sequence
	 */
	public static String runLengthFormatRepresentation(final boolean[] sequence) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[ ");

		if (sequence.length == 1) {
			builder.append(sequence[0] ? "+ 1" : "- 1");
		} else if (sequence.length > 1) {
			boolean lastSign = sequence[0];
			builder.append(lastSign ? "+" : "-");
			int signOccurences = 1;
			for (int i = 1; i < sequence.length; i++) {
				if (sequence[i] == lastSign) {
					signOccurences++;
				} else {
					builder.append(" ").append(signOccurences);
					lastSign = sequence[i];
					signOccurences = 1;
				}
			}
			builder.append(" ").append(signOccurences);
		}

		builder.append(" ]");
		return builder.toString();
	}

}
