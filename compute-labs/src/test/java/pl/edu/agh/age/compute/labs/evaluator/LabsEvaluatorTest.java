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

public final class LabsEvaluatorTest {

	private static final double ASSERTION_DELTA = 0.001;

	private LabsEvaluator evaluator;

	@Before public void setUp() {
		evaluator = new LabsEvaluator(EvaluatorCounter.empty());
	}

	@Test public void testLabsLength4Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length4MeritFactor(), within(ASSERTION_DELTA));
	}

	@Test public void testLabsLength5Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length5Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length5MeritFactor(), within(ASSERTION_DELTA));
	}

	@Test public void testLabsLength6Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length6Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length6MeritFactor(), within(ASSERTION_DELTA));
	}

	@Test public void testLabsLength7Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length7Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length7MeritFactor(), within(ASSERTION_DELTA));
	}

}
