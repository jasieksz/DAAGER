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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;

public final class LabsSteepestDescentLocalSearch extends AbstractLabsImprovement {

	public LabsSteepestDescentLocalSearch(final LabsEvaluator evaluator, final boolean useFastFlipAlgorithm) {
		super(evaluator, useFastFlipAlgorithm);
	}

	@Override public LabsSolution improve(final LabsSolution solution) {
		LabsSolution best = requireNonNull(solution);
		boolean improvement = true;

		while (improvement) {
			improvement = false;
			LabsSolution bestNeighbour = null;
			double bestNeighbourFitness = Double.MIN_VALUE;

			for (int i = 0; i < solution.length(); i++) {
				final LabsSolution flipped = getFlippedSolution(best, i);
				if (flipped.fitnessValue() > bestNeighbourFitness) {
					bestNeighbour = flipped;
					bestNeighbourFitness = flipped.fitnessValue();
				}
			}

			if (bestNeighbourFitness > best.fitnessValue()) {
				best = bestNeighbour;
				improvement = true;
			}
		}
		return best;
	}

}
