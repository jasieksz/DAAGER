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

package pl.edu.agh.age.compute.stream.problem.rastrigin;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class MyBenchmark {

	@BenchmarkMode(value = Mode.AverageTime) @Benchmark public void testRastriginEvaluator() {
		final RastriginEvaluator evaluator = new RastriginEvaluator();
		for (double i = -5.10; i < 5.10; i += 0.001) {
			for (double j = -5.10; j < 5.10; j += 0.001) {
				evaluator.evaluate(new double[] {i, j});
			}
		}
	}

	@BenchmarkMode(value = Mode.AverageTime) @Benchmark public void testRastriginEvaluatorWithCache() {
		final RastriginEvaluatorWithCache evaluator = new RastriginEvaluatorWithCache();
		for (double i = -5.10; i < 5.10; i += 0.001) {
			final double startYRange = -5.10;
			for (double j = -5.10; j < 5.10; j += 0.001) {
				if (j != startYRange) {
					evaluator.evaluate(j);
				} else {
					evaluator.evaluate(new double[] {i, j}, 1);
				}

			}
		}
	}

	public static void main(final String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder().include(MyBenchmark.class.getSimpleName())
			                                    .warmupIterations(5)
		                                        .measurementIterations(5)
		                                        .forks(1)
		                                        .build();

		new Runner(opt).run();
	}

}
