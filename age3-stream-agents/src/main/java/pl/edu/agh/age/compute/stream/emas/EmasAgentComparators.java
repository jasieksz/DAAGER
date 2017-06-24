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

package pl.edu.agh.age.compute.stream.emas;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Comparators for EMAS agents.
 */
public final class EmasAgentComparators {

	private EmasAgentComparators() {}

	/**
	 * Ordering from higher to lower fitness.
	 */
	public static Comparator<EmasAgent> higherFitness() {
		return (first, second) -> Double.compare(first.solution.fitnessValue(), second.solution.fitnessValue());
	}

	/**
	 * Ordering from lower to higher fitness.
	 */
	public static Comparator<EmasAgent> lowerFitness() {
		return (first, second) -> Double.compare(second.solution.fitnessValue(), first.solution.fitnessValue());
	}

	/**
	 * Higher fitness probabilistic comparator, where agents have winning probability proportional to their fitness.
	 */
	public static Comparator<EmasAgent> higherFitnessProbabilistic() {
		return (first, second) -> {
			final Random rand = ThreadLocalRandom.current();
			final double threshold = getFitnessProportion(first, second);
			return rand.nextDouble() <= threshold ? 1 : -1;

		};
	}

	/**
	 * Lower fitness probabilistic comparator, where agents have winning probability inversely proportional to their
	 * fitness.
	 */
	public static Comparator<EmasAgent> lowerFitnessProbabilistic() {
		return (first, second) -> {
			final Random rand = ThreadLocalRandom.current();
			final double threshold = 1 - getFitnessProportion(first, second);
			return rand.nextDouble() <= threshold ? 1 : -1;
		};
	}

	/**
	 * This method returns probability (from [0 ; 1] range) that the first agent should be chosen as the one having
	 * bigger fitness than the second agent.
	 *
	 * @param first
	 *        the first agent
	 * @param second
	 *        the second agent
	 * @return the fitness proportion
	 */
	private static double getFitnessProportion(final EmasAgent first, final EmasAgent second) {
		final double firstValue = first.solution.fitnessValue();
		final double secondValue = second.solution.fitnessValue();
		if (firstValue > 0 && secondValue > 0) { // both positive
			return firstValue / (firstValue + secondValue);
		}
		if (firstValue < 0 && secondValue < 0) { // both negative - we need to swap probabilities
			return secondValue / (firstValue + secondValue);
		}
		// different signs - we need to normalize values so that they are both grater than zero
		final double smaller = Math.min(firstValue, secondValue);
		final double delta = Math.max(1.0, 1.2 * Math.abs(smaller));
		return (firstValue + delta) / (firstValue + secondValue + 2 * delta);
	}
}
