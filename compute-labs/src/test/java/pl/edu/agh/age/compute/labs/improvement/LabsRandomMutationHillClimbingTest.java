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

package pl.edu.agh.age.compute.labs.improvement;


import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Test;

public class LabsRandomMutationHillClimbingTest {

	private static final int numberOfIterations = 20;

	private final LabsEvaluator evaluator = new LabsEvaluator(EvaluatorCounter.empty());

	@Test public void testRMHC() {
		// given
		final LabsRandomMutationHillClimbing improvement = new LabsRandomMutationHillClimbing(evaluator,
		                                                                                      numberOfIterations, true);
		LabsSolution solution = new LabsSolution(
			new boolean[] {true, false, true, false, true, false, true, true, false});
		solution = solution.withFitness(evaluator.evaluate(solution));

		// when
		final LabsSolution result = improvement.improve(solution);

		// then
		int differentBits = 0;
		for (int i = 0; i < solution.length(); i++) {
			if (solution.sequenceRepresentation()[i] != result.sequenceRepresentation()[i]) {
				differentBits++;
			}
		}

		assertThat(numberOfIterations >= differentBits).isTrue();
		assertThat(solution.fitnessValue() <= result.fitnessValue()).isTrue();
	}
}
