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

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.labs.LabsSequences;

import org.junit.Test;

@SuppressWarnings("ZeroLengthArrayAllocation")
public final class LabsSolutionPrinterTest {

	@Test public void testExactPrinting() {
		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {})).isEqualTo("[ ]");

		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {true})).isEqualTo("[ +1 ]");
		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {false})).isEqualTo("[ -1 ]");

		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {true, false})).isEqualTo("[ +1 -1 ]");
		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {false, true})).isEqualTo("[ -1 +1 ]");

		assertThat(LabsSolutionPrinter.exactStringRepresentation(new boolean[] {true, true, false, true, false, false}))
			.isEqualTo("[ +1 +1 -1 +1 -1 -1 ]");

		assertThat(LabsSolutionPrinter.exactStringRepresentation(
			LabsSequences.length4Sequence().sequenceRepresentation())).isEqualTo("[ +1 +1 +1 -1 ]");
		assertThat(LabsSolutionPrinter.exactStringRepresentation(
			LabsSequences.length5Sequence().sequenceRepresentation())).isEqualTo("[ -1 -1 -1 +1 -1 ]");
		assertThat(LabsSolutionPrinter.exactStringRepresentation(
			LabsSequences.length6Sequence().sequenceRepresentation())).isEqualTo("[ +1 +1 +1 +1 -1 +1 ]");
		assertThat(LabsSolutionPrinter.exactStringRepresentation(
			LabsSequences.length7Sequence().sequenceRepresentation())).isEqualTo("[ -1 -1 -1 +1 +1 -1 +1 ]");
	}

	@Test public void testRunLengthPrinting() {
		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(new boolean[] {})).isEqualTo("[  ]");

		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(new boolean[] {true})).isEqualTo("[ + 1 ]");
		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(new boolean[] {false})).isEqualTo("[ - 1 ]");

		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(new boolean[] {true, false})).isEqualTo(
			"[ + 1 1 ]");
		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(new boolean[] {false, true})).isEqualTo(
			"[ - 1 1 ]");

		assertThat(LabsSolutionPrinter.runLengthFormatRepresentation(
			new boolean[] {true, true, false, true, false, false})).isEqualTo("[ + 2 1 1 2 ]");

		assertThat(
			LabsSolutionPrinter.runLengthFormatRepresentation(LabsSequences.length4Sequence().sequenceRepresentation()))
			.isEqualTo("[ + 3 1 ]");
		assertThat(
			LabsSolutionPrinter.runLengthFormatRepresentation(LabsSequences.length5Sequence().sequenceRepresentation()))
			.isEqualTo("[ - 3 1 1 ]");
		assertThat(
			LabsSolutionPrinter.runLengthFormatRepresentation(LabsSequences.length6Sequence().sequenceRepresentation()))
			.isEqualTo("[ + 4 1 1 ]");
		assertThat(
			LabsSolutionPrinter.runLengthFormatRepresentation(LabsSequences.length7Sequence().sequenceRepresentation()))
			.isEqualTo("[ - 3 2 1 1 ]");
	}

}
