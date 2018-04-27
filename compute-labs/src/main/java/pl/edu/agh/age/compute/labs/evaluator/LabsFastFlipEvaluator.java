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

import pl.edu.agh.age.compute.labs.solution.LabsSolution;

/**
 * ValueFlip evaluator defined by Jose E. Gallardo, Carlos Cotta, Antonio J. Fernandez in A Memetic Algorithm for the
 * Low Autocorrelation Binary Sequence Problem, <a href="https://dl.acm.org/citation.cfm?id=1277195">
 * https://dl.acm.org/citation.cfm?id=1277195</a>
 */
public final class LabsFastFlipEvaluator {

	private final LabsSolution originalSolution;

	private final int size;

	private final int[][] computedProducts;

	private final int[] correlations;

	public LabsFastFlipEvaluator(final LabsSolution solution) {
		originalSolution = solution;
		size = solution.length();
		computedProducts = new int[size - 1][size - 1];
		correlations = new int[size - 1];
		fillArrays(solution.sequenceRepresentation());
	}

	public LabsSolution originalSolution() {
		return originalSolution;
	}

	public double evaluateFlipped(final int index) {
		double energy = 0.0;
		for (int i = 0; i < size - 1; i++) {
			double correlation = correlations[i];
			if (i < size - index - 1) {
				correlation -= 2 * computedProducts[i][index];
			}
			if (i < index) {
				correlation -= 2 * computedProducts[i][index - i - 1];
			}
			energy += correlation * correlation;
		}
		return LabsEvaluator.meritFactorOf(size, energy); // merit factor
	}

	private void fillArrays(final boolean[] representation) {
		for (int i = 0; i < size - 1; i++) {
			int sum = 0;
			for (int j = 0; j < size - 1 - i; j++) {
				final int product = multiply(representation[j], representation[j + i + 1]);
				computedProducts[i][j] = product;
				sum += product;
			}
			correlations[i] = sum;
		}
	}

	private static int multiply(final boolean first, final boolean second) {
		return (first != second) ? -1 : 1;
	}

}
