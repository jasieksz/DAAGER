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
import pl.edu.agh.age.compute.labs.evaluator.LabsFastFlipEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.emas.reproduction.improvement.Improvement;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;

abstract class AbstractLabsImprovement implements Improvement<LabsSolution> {

	protected final LabsEvaluator evaluator;

	private final boolean useFastFlipAlgorithm;

	private @Nullable LabsFastFlipEvaluator fastFlipEvaluator;

	public AbstractLabsImprovement(final LabsEvaluator evaluator, final boolean useFastFlipAlgorithm) {
		this.evaluator = requireNonNull(evaluator);
		this.useFastFlipAlgorithm = useFastFlipAlgorithm;
	}

	protected final LabsSolution getFlippedSolution(final LabsSolution solution, final int index) {
		final LabsSolution flipped = flipSolution(solution.sequenceRepresentation(), index);
		return evaluateFlippedSolution(solution, flipped, index);
	}

	private LabsSolution flipSolution(final boolean[] representation, final int index) {
		final boolean[] flipped = Arrays.copyOf(representation, representation.length);
		flipped[index] = !flipped[index];
		return new LabsSolution(flipped);
	}

	private LabsSolution evaluateFlippedSolution(final LabsSolution originalSolution,
	                                             final LabsSolution flippedSolution, final int index) {
		if (useFastFlipAlgorithm) {
			updateFastFlipEvaluator(originalSolution);
			return flippedSolution.withFitness(fastFlipEvaluator.evaluateFlipped(index));
		} else {
			return flippedSolution.withFitness(evaluator.evaluate(flippedSolution));
		}
	}

	private void updateFastFlipEvaluator(final LabsSolution solution) {
		if ((fastFlipEvaluator == null) || ((fastFlipEvaluator != null) && (fastFlipEvaluator.originalSolution()
		                                                                    != solution))) {
			fastFlipEvaluator = new LabsFastFlipEvaluator(solution);
		}
	}

}
