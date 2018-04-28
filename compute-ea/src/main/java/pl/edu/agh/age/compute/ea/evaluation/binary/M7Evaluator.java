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
/*
 * File: M7Evaluator.java


 */

package pl.edu.agh.age.compute.ea.evaluation.binary;

import pl.edu.agh.age.compute.ea.evaluation.Evaluator;
import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;

import io.vavr.collection.Array;

/**
 * A deceptive multimodal function M7.
 *
 * Function M7 has 32 global maxima of value equal to 5, and several million local maxima, the values of which are
 * between 3.203 and 4.641
 */
public final class M7Evaluator implements Evaluator<BooleanVectorSolution> {

	private static final long serialVersionUID = 838649713968144831L;

	@Override public double evaluate(final BooleanVectorSolution solution) {
		final Array<Boolean> representation = solution.unwrap();

		double sum = 0;
		for (int i = 0; i < 5; i++) {
			int count = 0;
			for (int j = 0; j < 6; j++) {
				if (representation.get(6 * i + j)) {
					count++;
				}
			}
			sum += u(count);
		}

		return sum;
	}

	private double u(final int count) {
		switch (count) {
			case 0:
				return 1;
			case 1:
				return 0;
			case 2:
				return 0.389049;
			case 3:
				return 0.640576;
			case 4:
				return 0.389049;
			case 5:
				return 0;
			case 6:
				return 1;
			default:
				throw new IllegalArgumentException("Unknown count. Must be in [0,6] range.");
		}

	}
}
