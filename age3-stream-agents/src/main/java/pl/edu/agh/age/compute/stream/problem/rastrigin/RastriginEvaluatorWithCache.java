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

public final class RastriginEvaluatorWithCache {

	private static final int A = 10;

	private double cache;

	public double evaluate(double[] representation, int ithToChange) {
		int n = representation.length;
		double res = A * n;
		double changableValue = 0.0;
		for (int i = 0; i < n; i++) {
			res += representation[i] * representation[i] - A * Math.cos(2 * Math.PI * representation[i]);
			if (i == ithToChange) {
				changableValue = representation[i] * representation[i] - A * Math.cos(2 * Math.PI * representation[i]);
			}
		}
		cache = res - changableValue;
		return res;
	}

	public double evaluate(double ithValue) {
		return cache + ithValue * ithValue - A * Math.cos(2 * Math.PI * ithValue);
	}


}
