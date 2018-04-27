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

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class LabsRandomMutationHillClimbing extends AbstractLabsImprovement {

	private final int iterations;

	public LabsRandomMutationHillClimbing(final LabsEvaluator evaluator, final int iterations,
	                                      final boolean useFastFlipAlgorithm) {
		super(evaluator, useFastFlipAlgorithm);
		this.iterations = iterations;
	}

	@Override public LabsSolution improve(final LabsSolution solution) {
		final Random rand = ThreadLocalRandom.current();
		LabsSolution best = solution;
		for (int i = 0; i < iterations; i++) {
			final int indexToFlip = rand.nextInt(solution.length());
			final LabsSolution flipped = getFlippedSolution(best, indexToFlip);
			if (flipped.fitnessValue() > best.fitnessValue()) {
				best = flipped;
			}
		}
		return best;
	}

}
