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

public class LabsEvaluatorWithCacheTest {
	private static final double ASSERTION_DELTA = 0.001;

	private LabsEvaluator evaluator;

	@Before public void setUp() {
		evaluator = new LabsEvaluatorWithCache(EvaluatorCounter.empty(), 100);
	}

	@Test public void testLabsLength4Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length4MeritFactor(), within(ASSERTION_DELTA));
	}

	@Test public void testLabsLength4EvaluatorTwiceTheSameSolution() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();
		evaluator.evaluate(sequence);

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length4MeritFactor(), within(ASSERTION_DELTA));
	}

}
