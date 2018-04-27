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

abstract class AbstractLabsSawSearch extends AbstractLabsImprovement {

	private final int iterations;

	public AbstractLabsSawSearch(final LabsEvaluator evaluator, final int iterations,
	                             final boolean useFastFlipAlgorithm) {
		super(evaluator, useFastFlipAlgorithm);
		this.iterations = iterations;
	}

	@Override public final LabsSolution improve(final LabsSolution solution) {
		LabsSolution best = solution;
		LabsSolution current = solution;

		clearWalkList();
		addToWalkList(current);

		for (int iteration = 0; iteration < iterations; iteration++) {
			LabsSolution bestNeighbour = null;
			double bestNeighbourFitness = Double.MIN_VALUE;

			for (int i = 0; i < solution.length(); i++) {
				final LabsSolution flipped = getFlippedSolution(current, i);
				if ((flipped.fitnessValue() > bestNeighbourFitness) && !isInWalkList(flipped)) {
					bestNeighbour = flipped;
					bestNeighbourFitness = flipped.fitnessValue();
				}
			}

			if (bestNeighbour != null) {
				// we found a solution in this iteration (not necessarily better than currently the best one)
				addToWalkList(bestNeighbour);
				current = bestNeighbour;
				if (bestNeighbourFitness > best.fitnessValue()) {
					// we found an improved solution (compared to the best known so far)
					best = bestNeighbour;
				}
			} else {
				// we are trapped -> no further steps can be made
				return best;
			}
		}

		return best;
	}

	protected abstract void clearWalkList();

	protected abstract void addToWalkList(final LabsSolution solution);

	protected abstract boolean isInWalkList(final LabsSolution solution);

}
