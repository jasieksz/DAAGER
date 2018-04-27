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

import org.junit.Test;

public final class LabsSolutionTest {

	private static final boolean[] LABS_BOOLEAN_1 = {true, true, true, true, false, false, true, false};

	private static final int[] LABS_INTEGER_1 = {1, 1, 1, 1, -1, -1, 1, -1};

	private static final int[] LABS_RUNLENGTH_1 = {4, 2, 1, 1};

	private static final boolean[] LABS_BOOLEAN_2 = {true, false, false, false, true, false, false, true};

	private static final int[] LABS_INTEGER_2 = {1, -1, -1, -1, 1, -1, -1, 1};

	private static final int[] LABS_RUNLENGTH_2 = {1, 3, 1, 2, 1};

	private static final boolean[] LABS_BOOLEAN_3 = {false, false, false, true, true, false, true};

	private static final int[] LABS_INTEGER_3 = {-1, -1, -1, 1, 1, -1, 1};

	private static final int[] LABS_RUNLENGTH_3 = {3, 2, 1, 1}; // skew-symmetric

	private static final boolean[] LABS_BOOLEAN_4 = {false, false, true, false, false, false, false, false};

	private static final int[] LABS_INTEGER_4 = {-1, -1, 1, -1, -1, -1, -1, -1};

	private static final int[] LABS_RUNLENGTH_4 = {2, 1, 5}; // skew-symmetric

	@Test public void testLabsDefaultConstructor() {
		final LabsSolution solution1 = new LabsSolution(LABS_BOOLEAN_1);
		final LabsSolution solution2 = new LabsSolution(LABS_BOOLEAN_2);
		final LabsSolution solution3 = new LabsSolution(LABS_BOOLEAN_3);
		final LabsSolution solution4 = new LabsSolution(LABS_BOOLEAN_4);

		assertSolutions(solution1, solution2, solution3, solution4);
	}

	@Test public void testLabsIntegerArrayConstructor() {
		final LabsSolution solution1 = new LabsSolution(LABS_INTEGER_1);
		final LabsSolution solution2 = new LabsSolution(LABS_INTEGER_2);
		final LabsSolution solution3 = new LabsSolution(LABS_INTEGER_3);
		final LabsSolution solution4 = new LabsSolution(LABS_INTEGER_4);

		assertSolutions(solution1, solution2, solution3, solution4);
	}

	@Test public void testLabsRunLengthConstructor() {
		final LabsSolution solution1 = new LabsSolution(LABS_RUNLENGTH_1, true);
		final LabsSolution solution2 = new LabsSolution(LABS_RUNLENGTH_2, true);
		final LabsSolution solution3 = new LabsSolution(LABS_RUNLENGTH_3, false);
		final LabsSolution solution4 = new LabsSolution(LABS_RUNLENGTH_4, false);

		assertSolutions(solution1, solution2, solution3, solution4);
	}

	private static void assertSolutions(final LabsSolution solution1, final LabsSolution solution2,
	                                    final LabsSolution solution3, final LabsSolution solution4) {
		assertThat(solution1.sequenceRepresentation()).isEqualTo(LABS_BOOLEAN_1);
		assertThat(solution2.sequenceRepresentation()).isEqualTo(LABS_BOOLEAN_2);
		assertThat(solution3.sequenceRepresentation()).isEqualTo(LABS_BOOLEAN_3);
		assertThat(solution4.sequenceRepresentation()).isEqualTo(LABS_BOOLEAN_4);
	}
}
