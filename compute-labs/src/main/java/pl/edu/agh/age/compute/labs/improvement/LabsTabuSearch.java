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

public final class LabsTabuSearch extends AbstractLabsImprovement {

	private final int iterations;

	private final int minTabu;

	private final int extraTabu;

	public LabsTabuSearch(final LabsEvaluator evaluator, final int iterations, final boolean useFastFlipAlgorithm) {
		super(evaluator, useFastFlipAlgorithm);
		this.iterations = iterations;
		minTabu = iterations / 10;
		extraTabu = iterations / 50;
		// Reference - http://www.lcc.uma.es/~afdez/Papers/labsASC.pdf
	}

	@Override public LabsSolution improve(final LabsSolution solution) {
		final Random rand = ThreadLocalRandom.current();
		final int size = solution.length();
		final int[] tabu = new int[size];

		LabsSolution best = solution;
		LabsSolution current = solution;

		for (int iteration = 0; iteration < iterations; iteration++) {
			int bestChangedBit = -1;
			LabsSolution bestNeighbour = null;
			double bestNeighbourFitness = Double.MIN_VALUE;

			for (int i = 0; i < size; i++) {
				final LabsSolution flipped = getFlippedSolution(current, i);
				if ((iteration >= tabu[i]) || (flipped.fitnessValue() > best.fitnessValue())) {
					if (flipped.fitnessValue() > bestNeighbourFitness) {
						bestChangedBit = i;
						bestNeighbour = flipped;
						bestNeighbourFitness = flipped.fitnessValue();
					}
				}
			}

			if (bestNeighbour != null) {
				current = bestNeighbour;
				tabu[bestChangedBit] = iteration + minTabu + ((extraTabu != 0) ? rand.nextInt(extraTabu) : 0);
			}
			if (bestNeighbourFitness > best.fitnessValue()) {
				best = bestNeighbour;
			}
		}
		return best;
	}

}
