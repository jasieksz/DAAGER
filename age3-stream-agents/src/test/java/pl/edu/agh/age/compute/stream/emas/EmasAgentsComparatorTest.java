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

package pl.edu.agh.age.compute.stream.emas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import pl.edu.agh.age.compute.stream.emas.solution.DoubleSolution;

import org.junit.Test;

import java.util.Comparator;
import java.util.Optional;

public class EmasAgentsComparatorTest {

	private static final int COMPARATOR_TEST_REPETITIONS = 100;

	public static EmasAgent createAgentWithFitness(final double fitness) {
		return EmasAgent.create(0.0, new DoubleSolution(0.0, fitness));
	}

	@Test public void testHigherFitnessComparator() {
		final Comparator<EmasAgent> comparator = EmasAgentComparators.higherFitness();
		final String comparatorName = "Higher fitness";

		testComparator(comparator, comparatorName, 5.0, 6.7, Optional.of(6.7));
		testComparator(comparator, comparatorName, 5.0, 5.0, Optional.of(5.0));
		testComparator(comparator, comparatorName, 7.5, 2.1, Optional.of(7.5));

		testComparator(comparator, comparatorName, 0.0, 0.0, Optional.of(0.0));
		testComparator(comparator, comparatorName, 0.0, 6.7, Optional.of(6.7));
		testComparator(comparator, comparatorName, 6.7, 0.0, Optional.of(6.7));

		testComparator(comparator, comparatorName, -6.0, -3.0, Optional.of(-3.0));
		testComparator(comparator, comparatorName, -3.14, 0.0, Optional.of(0.0));
		testComparator(comparator, comparatorName, 0.0, -7.46, Optional.of(0.0));
		testComparator(comparator, comparatorName, 44.0, -7.46, Optional.of(44.0));
		testComparator(comparator, comparatorName, -32.0, 7.46, Optional.of(7.46));
	}

	@Test public void testLowerFitnessComparator() {
		final Comparator<EmasAgent> comparator = EmasAgentComparators.lowerFitness();
		final String comparatorName = "Lower fitness";

		testComparator(comparator, comparatorName, 5.0, 6.7, Optional.of(5.0));
		testComparator(comparator, comparatorName, 5.0, 5.0, Optional.of(5.0));
		testComparator(comparator, comparatorName, 7.5, 2.1, Optional.of(2.1));

		testComparator(comparator, comparatorName, 0.0, 0.0, Optional.of(0.0));
		testComparator(comparator, comparatorName, 0.0, 6.7, Optional.of(0.0));
		testComparator(comparator, comparatorName, 6.7, 0.0, Optional.of(0.0));

		testComparator(comparator, comparatorName, -6.0, -3.0, Optional.of(-6.0));
		testComparator(comparator, comparatorName, -3.14, 0.0, Optional.of(-3.14));
		testComparator(comparator, comparatorName, 0.0, -7.46, Optional.of(-7.46));
		testComparator(comparator, comparatorName, 44.0, -7.46, Optional.of(-7.46));
		testComparator(comparator, comparatorName, -32.0, 7.46, Optional.of(-32.0));
	}

	@Test public void testHigherFitnessProbabilisticComparator() {
		final Comparator<EmasAgent> comparator = EmasAgentComparators.higherFitnessProbabilistic();
		final String comparatorName = "Higher fitness probabilistic";
		testProbabilisticComparators(comparator, comparatorName);
	}

	@Test public void testLowerFitnessProbabilisticComparator() {
		final Comparator<EmasAgent> comparator = EmasAgentComparators.lowerFitnessProbabilistic();
		final String comparatorName = "Lower fitness probabilistic";
		testProbabilisticComparators(comparator, comparatorName);
	}

	private void testProbabilisticComparators(final Comparator<EmasAgent> comparator, final String comparatorName) {
		testComparator(comparator, comparatorName, 5.0, 6.7);
		testComparator(comparator, comparatorName, 5.0, 5.0);
		testComparator(comparator, comparatorName, 7.5, 2.1);

		testComparator(comparator, comparatorName, 0.0, 0.0);
		testComparator(comparator, comparatorName, 0.0, 6.7);
		testComparator(comparator, comparatorName, 6.7, 0.0);

		testComparator(comparator, comparatorName, -6.0, -3.0);
		testComparator(comparator, comparatorName, -3.14, 0.0);
		testComparator(comparator, comparatorName, 0.0, -7.46);
		testComparator(comparator, comparatorName, 44.0, -7.46);
		testComparator(comparator, comparatorName, -32.0, 7.46);
	}

	private void testComparator(final Comparator<EmasAgent> comparator, final String comparatorName, final double first,
	                            final double second) {
		testComparator(comparator, comparatorName, first, second, Optional.empty());
	}

	private void testComparator(final Comparator<EmasAgent> comparator, final String comparatorName, final double first,
	                            final double second, final Optional<Double> expectedResult) {
		final Double expected = expectedResult.orElse(null);
		final EmasAgent firstAgent = createAgentWithFitness(first);
		final EmasAgent secondAgent = createAgentWithFitness(second);
		int firstCount = 0;
		int secondCount = 0;
		for (int i = 0; i < COMPARATOR_TEST_REPETITIONS; i++) {
			final double result = getBetterFitness(comparator, firstAgent, secondAgent);
			final boolean firstBetter = firstAgent.solution.fitnessValue() == result;
			firstCount = firstBetter ? firstCount + 1 : firstCount;
			secondCount = firstBetter ? secondCount : secondCount + 1;
			if (expected != null) {
				assertThat(expected).isEqualTo(result, offset(0.001));
			}
		}

		if (expected == null) {
			System.out.printf("%s comparator test\n", comparatorName);
			System.out.printf("%.2f (%dx) and %.2f (%dx)\n", first, firstCount, second, secondCount);
			System.out.println("--------------------------------------------");
		}
	}

	private double getBetterFitness(final Comparator<EmasAgent> comparator, final EmasAgent first,
	                                final EmasAgent second) {
		final int comparison = comparator.compare(first, second);
		final EmasAgent better = comparison > 0 ? first : second;
		return better.solution.fitnessValue();
	}

}
