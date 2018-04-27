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

package pl.edu.agh.age.compute.labs.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import pl.edu.agh.age.compute.labs.LabsSequences;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public final class LabsFastFlipEvaluatorTest {

	private static final double ASSERTION_DELTA = 0.001;

	private LabsEvaluator classicEvaluator;

	private LabsFastFlipEvaluator fastEvaluator;

	@Before public void setUp() {
		classicEvaluator = new LabsEvaluator(EvaluatorCounter.empty());
	}

	@Test public void testLabsOfLength4() {
		performTest(LabsSequences.length4Sequence());
	}

	@Test public void testLabsOfLength5() {
		performTest(LabsSequences.length5Sequence());
	}

	@Test public void testLabsOfLength6() {
		performTest(LabsSequences.length6Sequence());
	}

	@Test public void testLabsOfLength7() {
		performTest(LabsSequences.length7Sequence());
	}

	private void performTest(final LabsSolution solution) {
		fastEvaluator = new LabsFastFlipEvaluator(solution);
		for (int i = 0; i < solution.length(); i++) {
			final double classicEval = classicEvaluator.evaluate(flipSolution(solution, i));
			final double fastEval = fastEvaluator.evaluateFlipped(i);
			assertThat(classicEval).isEqualTo(fastEval, within(ASSERTION_DELTA));
		}
	}

	private static LabsSolution flipSolution(final LabsSolution solution, final int index) {
		final boolean[] flipped = Arrays.copyOf(solution.sequenceRepresentation(), solution.length());
		flipped[index] = !flipped[index];
		return new LabsSolution(flipped);
	}

}
