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

package pl.edu.agh.age.compute.ogr.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import pl.edu.agh.age.compute.ogr.RulerUtils;
import pl.edu.agh.age.compute.ogr.solution.Ruler;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Before;
import org.junit.Test;

public final class RulerEvaluatorTest {

	private static final double JUNIT_ALLOWED_DOUBLE_DELTA = 0.0001;

	private static final int[] PROPER_RULER_ORDER_1 = {0};

	private static final int PROPER_RULER_ORDER_1_LENGTH = 0;

	private static final int[] PROPER_RULER_ORDER_2 = {0, 1};

	private static final int PROPER_RULER_ORDER_2_LENGTH = 1;

	private static final int[] PROPER_RULER_ORDER_3 = {0, 1, 3};

	private static final int PROPER_RULER_ORDER_3_LENGTH = 3;

	private static final int[] PROPER_RULER_ORDER_10 = {0, 1, 6, 10, 23, 26, 34, 41, 53, 55};

	private static final int PROPER_RULER_ORDER_10_LENGTH = 55;

	private static final int[] INVALID_RULER_ORDER_3 = {0, 1, 2};

	private static final int INVALID_RULER_ORDER_3_VIOLATIONS = 1;

	private static final int INVALID_RULER_ORDER_3_LENGTH = 2;

	private static final int[] INVALID_RULER_ORDER_4 = {0, 1000, 2000, 4000};

	private static final int INVALID_RULER_ORDER_4_VIOLATIONS = 2;

	private static final int INVALID_RULER_ORDER_4_LENGTH = 4000;

	private static final int[] INVALID_RULER_ORDER_10 = {0, 1, 7, 10, 23, 33, 34, 41, 53, 55};

	private static final int INVALID_RULER_ORDER_10_VIOLATIONS = 8;

	private static final int INVALID_RULER_ORDER_10_LENGTH = 55;

	private RulerEvaluator evaluator;

	@Before public void setUp() {
		evaluator = new RulerEvaluator(EvaluatorCounter.empty());
	}

	@Test public void testProperRulers() {
		testProperRuler(PROPER_RULER_ORDER_1, PROPER_RULER_ORDER_1_LENGTH);
		testProperRuler(PROPER_RULER_ORDER_2, PROPER_RULER_ORDER_2_LENGTH);
		testProperRuler(PROPER_RULER_ORDER_3, PROPER_RULER_ORDER_3_LENGTH);
		testProperRuler(PROPER_RULER_ORDER_10, PROPER_RULER_ORDER_10_LENGTH);
	}

	@Test public void testInvalidRulers() {
		testInvalidRuler(INVALID_RULER_ORDER_3, INVALID_RULER_ORDER_3_LENGTH, INVALID_RULER_ORDER_3_VIOLATIONS);
		testInvalidRuler(INVALID_RULER_ORDER_4, INVALID_RULER_ORDER_4_LENGTH, INVALID_RULER_ORDER_4_VIOLATIONS);
		testInvalidRuler(INVALID_RULER_ORDER_10, INVALID_RULER_ORDER_10_LENGTH, INVALID_RULER_ORDER_10_VIOLATIONS);
	}

	private void testProperRuler(final int[] rulerRepresentation, final int expectedLength) {
		// Given
		final Ruler ruler = new Ruler(rulerRepresentation, true);

		// When
		final int violations = RulerUtils.calculateViolations(ruler);
		final double fitness = evaluator.evaluate(ruler);

		// Then
		assertThat(RulerUtils.isValid(ruler)).isTrue();
		assertThat(violations).isEqualTo(0);
		assertThat(fitness).isEqualTo(expectedLength, within(JUNIT_ALLOWED_DOUBLE_DELTA));
	}

	private void testInvalidRuler(final int[] rulerRepresentation, final int expectedLength,
	                              final int expectedViolations) {
		// Given
		final Ruler ruler = new Ruler(rulerRepresentation, true);

		// When
		final int violations = RulerUtils.calculateViolations(ruler);
		final double fitness = evaluator.evaluate(ruler);

		// Then
		assertThat(RulerUtils.isValid(ruler)).isFalse();
		assertThat(violations).isEqualTo(expectedViolations);
		assertThat(fitness).isEqualTo(expectedLength + RulerEvaluator.VIOLATION_PENALTY * expectedViolations,
		                              within(JUNIT_ALLOWED_DOUBLE_DELTA));
	}
}
