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

import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.Arrays;

import io.vavr.collection.Array;

public final class LabsSolution implements Solution<Array<Boolean>> {

	public static final boolean RUN_LENGTH_DISPLAY_FORMAT = true;

	private static final long serialVersionUID = -5293799727799766020L;

	private final boolean[] sequenceRepresentation;

	private double fitness = Double.NaN;

	/**
	 * Instantiates a new LABS solution.
	 *
	 * @param sequenceRepresentation
	 * 		the boolean sequence representation array, where <code>true</code> represents
	 * 		"1" value and <code>false</code> represents "-1"
	 */
	public LabsSolution(final boolean[] sequenceRepresentation) {
		this.sequenceRepresentation = Arrays.copyOf(sequenceRepresentation, sequenceRepresentation.length);
	}

	/**
	 * Instantiates a new LABS solution.
	 *
	 * @param sequenceRepresentation
	 * 		the sequence integer representation array, in which each each value is either "-1" or "1"
	 */
	public LabsSolution(final int[] sequenceRepresentation) {
		this.sequenceRepresentation = new boolean[sequenceRepresentation.length];
		for (int i = 0; i < sequenceRepresentation.length; i++) {
			this.sequenceRepresentation[i] = sequenceRepresentation[i] == 1;
		}
	}

	/**
	 * Instantiates a new LABS solution.
	 *
	 * @param runLengthSequenceRepresentation
	 * 		the run length sequence representation
	 * @param firstSign
	 * 		the first sign (positive or negative)
	 */
	public LabsSolution(final int[] runLengthSequenceRepresentation, final boolean firstSign) {
		final int length = Arrays.stream(runLengthSequenceRepresentation).sum();
		sequenceRepresentation = new boolean[length];
		boolean sign = firstSign;
		int filled = 0;
		for (final int count : runLengthSequenceRepresentation) {
			for (int i = 0; i < count; i++) {
				sequenceRepresentation[filled++] = sign;
			}
			sign = !sign;
		}
	}

	public boolean[] sequenceRepresentation() {
		return Arrays.copyOf(sequenceRepresentation, sequenceRepresentation.length);
	}

	public int length() {
		return sequenceRepresentation.length;
	}

	@Override public double fitnessValue() {
		return fitness;
	}

	@Override public LabsSolution withFitness(final double fitnessValue) {
		fitness = fitnessValue;
		return this;
	}

	@Override public String toString() {
		return RUN_LENGTH_DISPLAY_FORMAT
		       ? LabsSolutionPrinter.runLengthFormatRepresentation(sequenceRepresentation)
		       : LabsSolutionPrinter.exactStringRepresentation(sequenceRepresentation);
	}

	@Override public Array<Boolean> unwrap() {
		return Array.ofAll(sequenceRepresentation);
	}

}
