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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

import io.vavr.collection.List;

public final class Selectors {

	private Selectors() {}

	public static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> random() {
		return (population, count) -> {
			checkArgument(count <= population.size());
			final Random rand = ThreadLocalRandom.current();
			List<EmasAgent> availableAgents = List.ofAll(population);
			List<EmasAgent> selected = List.empty();
			for (int i = 0; i < count; i++) {
				final int index = rand.nextInt(availableAgents.size());
				selected = selected.append(availableAgents.get(index));
				availableAgents = availableAgents.removeAt(index);
			}
			return selected;
		};
	}

	public static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> highestFitness() {
		// We use a reverse comparator here so that the best agents are at the list beginning
		final Comparator<EmasAgent> comparator = EmasAgentComparators.lowerFitness();
		return (population, count) -> {
			checkArgument(count <= population.size());
			return population.sorted(comparator).take(count).toList();
		};
	}

	public static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> lowestFitness() {
		// We use a reverse comparator here so that the best agents are at the list beginning
		final Comparator<EmasAgent> comparator = EmasAgentComparators.higherFitness();
		return (population, count) -> {
			checkArgument(count <= population.size());
			return population.sorted(comparator).take(count).toList();
		};
	}

	public static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> highestFitnessProbabilistic() {
		return probabilisticFitness(true);
	}

	public static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> lowestFitnessProbabilistic() {
		return probabilisticFitness(false);
	}

	private static BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> probabilisticFitness(final boolean maximation) {
		return (population, count) -> {
			checkArgument(count <= population.size());
			final Random rand = ThreadLocalRandom.current();
			List<EmasAgent> availableAgents = List.ofAll(population);
			List<EmasAgent> selected = List.empty();
			int i = 0;

			while (i < count) {
				final List<Double> fitnesses = resolveNormalizedFitnessValues(availableAgents);
				final List<Double> intervals = resolveProbabilityIntervals(fitnesses, maximation);
				final double randomValue = rand.nextDouble();
				final int index = binarySearch(intervals, randomValue);
				selected = selected.append(availableAgents.get(index));
				availableAgents = availableAgents.removeAt(index);
				i++;
			}
			return selected;
		};
	}

	private static List<Double> resolveNormalizedFitnessValues(final List<EmasAgent> agents) {
		// We have to make sure that all fitness values are grater than zero for latter calculations
		final List<Double> fitnesses = agents.map(agent -> agent.solution.fitnessValue()).toList();
		final double minimum = fitnesses.min().get();
		final double maximum = fitnesses.max().get();
		if (minimum > 0) {
			// all values positive
			return fitnesses;
		} else if (maximum < 0) {
			// all values negative
			return fitnesses.map(fitness -> -1.0 / fitness).toList();
		}
		// values negative & positive or containing zeros
		final double delta = Math.max(1.0, 1.2 * Math.abs(minimum));
		return fitnesses.map(fitness -> fitness + delta).toList();
	}

	private static List<Double> resolveProbabilityIntervals(final List<Double> normalizedFitnesses,
	                                                        final boolean maximation) {
		final double total = getTotalSum(normalizedFitnesses, maximation);
		List<Double> intervals = List.empty();
		double sum = 0;
		for (int i = 0; i < normalizedFitnesses.size(); i++) {
			final double proportion = getProportionForIndex(normalizedFitnesses, i, total, maximation);
			intervals = intervals.append(sum + proportion);
			sum += proportion;
		}
		return intervals;
	}

	private static double getTotalSum(final List<Double> normalizedFitnesses, final boolean maximation) {
		if (maximation) {
			return normalizedFitnesses.sum().doubleValue();
		}
		return normalizedFitnesses.map(f -> 1.0 / f).sum().doubleValue();
	}

	private static double getProportionForIndex(final List<Double> normalizedFitnesses, final int i, final double total,
	                                            final boolean maximation) {
		final double fitness = maximation ? normalizedFitnesses.get(i) : 1.0 / normalizedFitnesses.get(i);
		return fitness / total;
	}

	private static int binarySearch(final List<Double> sortedValues, final double key) {
		// We look for an index under which the value is equal or just above the required key
		int lower = 0;
		int upper = sortedValues.size() - 1;

		while (lower <= upper) {
			final int mid = (lower + upper) >>> 1;
			final double midVal = sortedValues.get(mid);

			if (midVal < key) {
				lower = mid + 1;
			} else if (midVal > key) {
				upper = mid - 1;
			} else {
				return mid; // key found
			}
		}
		return lower; // key not found
	}
}
