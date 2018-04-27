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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.problem.LabsProblem;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface LabsSolutionFactory {

	LabsSolution create();

	static LabsSolutionFactory random(final LabsProblem problemDefinition, final LabsEvaluator evaluator) {
		requireNonNull(problemDefinition);
		requireNonNull(evaluator);

		return () -> {
			final Random rand = ThreadLocalRandom.current();
			final int length = problemDefinition.problemSize();
			final boolean[] sequence = new boolean[length];

			for (int i = 0; i < length; i++) {
				sequence[i] = rand.nextBoolean();
			}

			final LabsSolution solution = new LabsSolution(sequence);
			return solution.withFitness(evaluator.evaluate(solution));
		};
	}

}
