/*
 * Copyright (C) 2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.stream.problem.rastrigin;

import pl.edu.agh.age.compute.stream.emas.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

public final class RastriginEvaluator implements Evaluator<DoubleVectorSolution, Double> {
	private static final int A = 10;

	@Override public Double evaluate(final DoubleVectorSolution solution) {
		return evaluate(solution.values());
	}

	public double evaluate(final double[] representation) {
		final int n = representation.length;
		double res = A * n;
		for (int i = 0; i < n; i++) {
			res += (representation[i] * representation[i]) - (A * Math.cos(2 * Math.PI * representation[i]));
		}
		return res;
	}
}