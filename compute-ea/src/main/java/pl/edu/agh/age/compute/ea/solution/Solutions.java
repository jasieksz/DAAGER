/*
 * Copyright (C) 2016-2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.ea.solution;

import pl.edu.agh.age.compute.ea.problem.ParallelProblem;
import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;

import java.io.Serializable;

/**
 * Utilities and factory methods for solutions.
 */
public final class Solutions {
	private Solutions() {}

	public static <T extends Serializable> SimpleSolution<T> simple(final T value) {
		return new SimpleSolution<>(value);
	}

	public static DoubleSolution singleDouble(final double value) {
		return new DoubleSolution(value);
	}

	public static DoubleVectorSolution randomDoubleVectorForProblem(final ParallelProblem<Double> problem,
	                                                                final NormalizedDoubleRandomGenerator randomGenerator) {
		final double[] representation = new double[problem.dimension()];
		for (int i = 0; i < representation.length; i++) {
			representation[i] = problem.lowerBound(i) + (randomGenerator.nextDouble() * (problem.upperBound(i) - problem
				                                                                                                     .lowerBound(
					                                                                                                     i)));
		}
		return new DoubleVectorSolution(representation);
	}

	public static IntegerVectorSolution randomIntegerVectorForProblem(final ParallelProblem<Integer> problem,
	                                                                  final IntRandomGenerator randomGenerator) {
		final int[] representation = new int[problem.dimension()];
		for (int i = 0; i < representation.length; i++) {
			representation[i] = problem.lowerBound(i) + randomGenerator.nextInt(
				(problem.upperBound(i) + 1) - problem.lowerBound(i));
		}
		return new IntegerVectorSolution(representation);
	}

	public static BooleanVectorSolution randomBooleanVectorForProblem(final ParallelProblem<Boolean> problem,
	                                                                  final NormalizedDoubleRandomGenerator randomGenerator) {
		final boolean[] representation = new boolean[problem.dimension()];
		for (int i = 0; i < representation.length; i++) {
			representation[i] = randomGenerator.nextDouble() > 0.5;
		}
		return new BooleanVectorSolution(representation);
	}
}
