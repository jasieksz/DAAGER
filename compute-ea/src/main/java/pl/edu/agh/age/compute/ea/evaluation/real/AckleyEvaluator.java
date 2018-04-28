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
 * This class represents a floating-point coded Ackley function.
 *
 * Solution: min=0.0, xi=0, i=1..n
 *
 * http://tracer.lcc.uma.es/problems/ackley/ackley.html
 *
 * The original problem is a minimalization one but it is much convenient to maximize the problem function. So the
 * original function is modified g(x)=-f(x)
 */
public final class AckleyEvaluator implements Evaluator<DoubleVectorSolution> {

	private static final long serialVersionUID = 2086324944363720301L;

	@Override public double evaluate(final DoubleVectorSolution solution) {
		final double[] values = solution.valuesAsPrimitive();
		final int n = values.length;

		double sum1 = 0;
		double sum2 = 0;
		for (final double value : values) {
			sum1 += value * value;
			sum2 += Math.cos(2 * Math.PI * value);
		}

		return 20 * Math.exp(-0.2 * Math.sqrt((1.0 / n) * sum1)) + Math.exp((1.0 / n) * sum2) - 20 - Math.exp(1);
	}
}
