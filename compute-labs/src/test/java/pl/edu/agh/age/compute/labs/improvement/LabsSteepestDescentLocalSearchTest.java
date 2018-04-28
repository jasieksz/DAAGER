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
import static org.assertj.core.api.Assertions.within;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Test;

import java.util.Arrays;

public class LabsSteepestDescentLocalSearchTest {
	final LabsEvaluator evaluator = new LabsEvaluator(EvaluatorCounter.empty());

	@Test public void testSDLS() {
		// given
		LabsSteepestDescentLocalSearch improvement = new LabsSteepestDescentLocalSearch(evaluator, true);
		LabsSolution solution = new LabsSolution(new boolean[] {true, true, true, true, true});
		solution = solution.withFitness(evaluator.evaluate(solution));

		// when
		LabsSolution result = improvement.improve(solution);

		// then
		boolean[] exp1 = {true, false, true, true, true};
		boolean[] exp2 = {true, false, true, true, true};
		assertThat(Arrays.equals(exp1, result.sequenceRepresentation()) || Arrays.equals(exp2,
		                                                                                 result.sequenceRepresentation()))
			.isTrue();
		assertThat(result.fitnessValue()).isEqualTo(evaluator.evaluate(new LabsSolution(exp1)), within(0.001));
	}
}
