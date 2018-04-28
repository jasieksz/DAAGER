/*
 * Copyright (C) 2006-2018 Intelligent Information Systems Group.
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


package pl.edu.agh.age.compute.ea.evaluation.real;

import pl.edu.agh.age.compute.ea.evaluation.Evaluator;
import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;

/**
 * This class represents a floating-point coded Rosenbrock function.
 *
 * Solution: min=0.0, xi=0, i=1..n
 *
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2537.htm
 *
 * The original problem is a minimalization one but it is much convenient to maximize the problem function. So the
 * original function is modified g(x)=-f(x)
 */
public final class RosenbrockEvaluator implements Evaluator<DoubleVectorSolution> {

	@Override public double evaluate(final DoubleVectorSolution solution) {
		final double[] values = solution.valuesAsPrimitive();

		double sum = 0;
		for (int i = 0, n = values.length; i < n - 1; i++) {
			final double value = values[i];
			final double nextValue = values[i + 1];
			sum += 100.0 * Math.pow(value * value - nextValue, 2) + Math.pow(value - 1, 2);
		}

		return -sum;
	}
}
