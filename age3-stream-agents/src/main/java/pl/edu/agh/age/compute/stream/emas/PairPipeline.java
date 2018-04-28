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

import java.util.function.Function;
import java.util.function.Predicate;

import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

/**
 * Processing definition for pairs of EmasAgent.
 *
 * @see Pipeline
 */
public final class PairPipeline {

	private final List<Tuple2<EmasAgent, EmasAgent>> pairs;

	PairPipeline(final List<Tuple2<EmasAgent, EmasAgent>> pairs) {
		this.pairs = pairs;
	}

	/**
	 * Reproduce in pairs.
	 *
	 * @param reproductionStrategy
	 * 		a function taking a pair of agents as an argument and returning a new set of agents that should replace this
	 * 		pair. Parents should also be returned in this set if they should stay in the new population.
	 *
	 * @return the tuple containing parent and child agents pipelines
	 */
	public Tuple2<Pipeline, Pipeline> reproduce(final Function<Tuple2<EmasAgent, EmasAgent>, Tuple2<Seq<EmasAgent>, EmasAgent>> reproductionStrategy) {
		final List<Tuple2<Seq<EmasAgent>, EmasAgent>> ts = pairs.map(reproductionStrategy);
		return PipelineUtils.flattenToPipelineTuple(ts);
	}

	public Tuple2<Pipeline, Pipeline> reproduce(final Function2<EmasAgent, EmasAgent, Tuple2<Seq<EmasAgent>, EmasAgent>> reproductionStrategy) {
		return reproduce(reproductionStrategy.tupled());
	}

	/**
	 * Fight in pairs.
	 *
	 * @param fightingStrategy
	 * 		a function taking a pair of agents as an argument and returning a new set of agents that should replace this
	 * 		pair.
	 *
	 * @return a new Pipeline for new population
	 */
	public Pipeline fight(final Function<Tuple2<EmasAgent, EmasAgent>, Seq<EmasAgent>> fightingStrategy) {
		final List<EmasAgent> ts = pairs.flatMap(fightingStrategy).distinct();
		return new Pipeline(ts);
	}

	public Pipeline fight(final Function2<EmasAgent, EmasAgent, Seq<EmasAgent>> fightingStrategy) {
		return fight(fightingStrategy.tupled());
	}

	/**
	 * Performs an agent encounter. If both agents meet a given reproduction predicate - they reproduce themselves with
	 * a given reproduction strategy. Otherwise they fight with each other with a given fight strategy.
	 *
	 * @param reproductionPredicate
	 * 		the reproduction predicate
	 * @param reproductionStrategy
	 * 		a function taking a pair of agents as an argument and returning a new set of agents that should replace
	 * 		this pair. Parents should also be returned in this set if they should stay in the new population.
	 * @param fightingStrategy
	 * 		a function taking a pair of agents as an argument and returning a new set of agents that should replace
	 * 		this pair.
	 *
	 * @return a new Pipeline for new population
	 */
	public Tuple2<Pipeline, Pipeline> encounter(final Predicate<EmasAgent> reproductionPredicate,
	                          final Function<Tuple2<EmasAgent, EmasAgent>, Tuple2<Seq<EmasAgent>, EmasAgent>> reproductionStrategy,
	                          final Function<Tuple2<EmasAgent, EmasAgent>, Seq<EmasAgent>> fightingStrategy) {
		final List<Tuple2<Seq<EmasAgent>, EmasAgent>> ts = pairs.map(
			pair -> encounterPair(pair, reproductionPredicate, reproductionStrategy, fightingStrategy));
		return PipelineUtils.flattenToPipelineTuple(ts);
	}

	private static Tuple2<Seq<EmasAgent>, EmasAgent> encounterPair(final Tuple2<EmasAgent, EmasAgent> pair,
	                                            final Predicate<EmasAgent> reproductionPredicate,
	                                            final Function<Tuple2<EmasAgent, EmasAgent>, Tuple2<Seq<EmasAgent>, EmasAgent>> reproductionStrategy,
	                                            final Function<Tuple2<EmasAgent, EmasAgent>, Seq<EmasAgent>> fightingStrategy) {
		if (reproductionPredicate.test(pair._1) && reproductionPredicate.test(pair._2)) {
			return reproductionStrategy.apply(pair); // reproduce
		} else {
			return Tuple.of(fightingStrategy.apply(pair), null); // fight
		}
	}
}
