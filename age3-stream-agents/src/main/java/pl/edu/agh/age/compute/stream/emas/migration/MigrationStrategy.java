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

package pl.edu.agh.age.compute.stream.emas.migration;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Selectors;

import java.util.function.BiFunction;

import io.vavr.collection.List;

/**
 * The strategies for agents migration.
 *
 * Deterministic strategies (random, highest fitness and lowest fitness) are quite obvious in their behavior.
 * Probabilistic strategies choose agents based on their evaluation quality. Agents with a better evaluation have higher
 * probability of being chosen, and agents with a worse one - have smaller probability. There is no guarantee that agent
 * with the best evaluation of all available will be chosen in probabilistic strategies.
 *
 * IMPORTANT NOTICE - probabilistic selectors behave best when fitness values consist of only positive or only negative
 * numbers (excluding zeros). If the solution evaluator can output both positive and negative fitnesses - consider using
 * non-probabilistic selectors for more accurate selection, as the probabilistic ones tend to have rather uniform
 * probability distribution in such situations.
 */
@SuppressWarnings("ImmutableEnumChecker")
public enum MigrationStrategy {

	RANDOM(Selectors.random()),
	HIGHEST_FITNESS(Selectors.highestFitness()),
	HIGHEST_FITNESS_PROBABILISTIC(Selectors.highestFitnessProbabilistic()),
	LOWEST_FITNESS(Selectors.lowestFitness()),
	LOWEST_FITNESS_PROBABILISTIC(Selectors.lowestFitnessProbabilistic());

	private final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector;

	MigrationStrategy(final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector) {
		this.selector = selector;
	}

	public BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector() {
		return selector;
	}

}
