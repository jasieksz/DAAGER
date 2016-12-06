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

import java.util.function.BiFunction;
import java.util.function.Predicate;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

/**
 * EMAS agent processing definition.
 *
 * Pipeline is similar to Java Streams, but its methods are executed when called and *not* lazily computed when needed.
 * This implementation is immutable - each call returns a new pipeline instance or a tuple of new instances.
 *
 * @see pl.edu.agh.age.compute.stream.Pipeline
 */
public final class Pipeline extends pl.edu.agh.age.compute.stream.Pipeline<EmasAgent, Pipeline> {

	// PairPipeline requires package-protected access
	Pipeline(final List<EmasAgent> population) {
		super(population, Pipeline::new);
	}

	/**
	 * Create a new pipeline for the given population.
	 *
	 * @param population
	 * 		population of EMAS agents.
	 *
	 * @return a new pipeline instance.
	 */
	public static Pipeline on(final List<EmasAgent> population) {
		return new Pipeline(population);
	}

	/**
	 * Select pairs from the current population in this pipeline with regards to the given selector function.
	 *
	 * @param selector
	 * 		a function of two arguments. The first argument is an agent and the second one - the rest of the current
	 * 		population. The selector should return a pair of agents selected for this agent. Usually it will be the given
	 * 		agent and another one.
	 *
	 * @return a pipeline operating on pairs of agents.
	 *
	 * @see Selectors
	 */
	public PairPipeline selectPairsWithRepetitions(final BiFunction<EmasAgent, List<EmasAgent>, Tuple2<EmasAgent, EmasAgent>> selector) {
		List<EmasAgent> remainingAgents = population;
		List<Tuple2<EmasAgent, EmasAgent>> pairs = List.empty();

		while (remainingAgents.size() > 1) {
			final EmasAgent agent = remainingAgents.get();
			remainingAgents = remainingAgents.remove(agent);

			final Tuple2<EmasAgent, EmasAgent> selectedPair = selector.apply(agent, remainingAgents);
			pairs = pairs.append(selectedPair);
		}
		return new PairPipeline(pairs);
	}

	/**
	 * Select pairs from the current population in this pipeline with regards to the given selector function.
	 *
	 * @param selector
	 * 		a function of two arguments. The first argument is an agent and the second one - the rest of the current
	 * 		population. The selector should return a pair of agents selected for this agent. Usually it will be the given
	 * 		agent and another one.
	 *
	 * @return a pair of pipelines - the first one is a {@link PairPipeline} containing the selected pairs, the second
	 * one - pipeline containing all agents which have not been selected.
	 *
	 * @see Selectors
	 */
	public Tuple2<PairPipeline, Pipeline> selectPairs(final BiFunction<EmasAgent, List<EmasAgent>, Tuple2<EmasAgent, EmasAgent>> selector) {
		List<EmasAgent> remainingAgents = population;
		List<Tuple2<EmasAgent, EmasAgent>> pairs = List.empty();

		while (remainingAgents.size() > 1) {
			final EmasAgent agent = remainingAgents.get();
			remainingAgents = remainingAgents.remove(agent);

			final Tuple2<EmasAgent, EmasAgent> selectedPair = selector.apply(agent, remainingAgents);
			remainingAgents = remainingAgents.remove(selectedPair._2);

			pairs = pairs.append(selectedPair);
		}
		return Tuple.of(new PairPipeline(pairs), new Pipeline(remainingAgents));
	}

	/**
	 * Population splitter for migration.
	 *
	 * @param migrationPredicate
	 * 		a predicate that should return true if an agent should be migrated.
	 *
	 * @return a pair of pipelines - the first one contains agents for migration, the second one - agents staying in
	 * this population.
	 *
	 * @see Predicates
	 */
	public Tuple2<Pipeline, Pipeline> migrateWhen(final Predicate<EmasAgent> migrationPredicate) {
		return split(migrationPredicate);
	}

	/**
	 * Population splitter for death.
	 *
	 * @param deathPredicate
	 * 		a predicate that should return true if an agent should die.
	 *
	 * @return a pair of pipelines - the first one contains agents that are dead, the second one - agents staying in
	 * this population.
	 *
	 * @see Predicates
	 */
	public Tuple2<Pipeline, Pipeline> dieWhen(final Predicate<EmasAgent> deathPredicate) {
		return split(deathPredicate);
	}

}
