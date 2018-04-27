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

package pl.edu.agh.age.compute.ogr.solution;

import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.Arrays;
import java.util.List;

import io.vavr.collection.Array;

public final class Ruler implements Solution<Array<Integer>> {

	public static final boolean DIRECT_REPRESENTATION_FORMAT = true;

	private static final long serialVersionUID = -2879483485526990936L;

	private final int[] directRepresentation; // mark representation (length == order)

	private final int[] indirectRepresentation; // measurable distances representation (length == order - 1)

	private double fitness = Double.NaN;

	public Ruler(final List<Integer> marks, final boolean isDirectRepresentation) {
		this(marks.stream().mapToInt(i -> i).toArray(), isDirectRepresentation);
	}

	public Ruler(final int[] marks) {
		this(marks, true);
	}

	public Ruler(final int[] marks, final boolean isDirectRepresentation) {
		if (isDirectRepresentation) {
			directRepresentation = Arrays.copyOf(marks, marks.length);
			indirectRepresentation = new int[marks.length - 1];
			for (int i = 1; i < marks.length; i++) {
				final int distance = directRepresentation[i] - directRepresentation[i - 1];
				indirectRepresentation[i - 1] = distance;
			}
		} else {
			indirectRepresentation = Arrays.copyOf(marks, marks.length);
			directRepresentation = new int[marks.length + 1];
			directRepresentation[0] = 0;
			for (int i = 1; i < (marks.length + 1); i++) {
				directRepresentation[i] = indirectRepresentation[i - 1] + directRepresentation[i - 1];
			}
		}
	}

	public int[] directRepresentation() {
		return Arrays.copyOf(directRepresentation, directRepresentation.length);
	}

	public int[] indirectRepresentation() {
		return Arrays.copyOf(indirectRepresentation, indirectRepresentation.length);
	}

	/**
	 * This method returns <b>Golumb Ruler length</b>. One should bear in mind, that this value has nothing to do
	 * with marks count on the ruler. If you want to obtain marks count value, use {@link #marksCount()} method instead.
	 *
	 * @return the Golumb Ruler length
	 */
	public int length() {
		return directRepresentation[directRepresentation.length - 1];
	}

	/**
	 * This method returns marks count on the direct representation of a ruler.
	 *
	 * @return the marks count
	 */
	public int marksCount() {
		return directRepresentation.length;
	}

	@Override public double fitnessValue() {
		return fitness;
	}

	@Override public Ruler withFitness(final double fitnessValue) {
		fitness = fitnessValue;
		return this;
	}

	@Override public String toString() {
		return DIRECT_REPRESENTATION_FORMAT ? toString(directRepresentation) : toString(indirectRepresentation);
	}

	private String toString(final int[] array) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (final int element : array) {
			builder.append(" ").append(element);
		}
		builder.append(" ]");
		return builder.toString();
	}

	@Override public Array<Integer> unwrap() {
		return Array.ofAll(directRepresentation);
	}

}
