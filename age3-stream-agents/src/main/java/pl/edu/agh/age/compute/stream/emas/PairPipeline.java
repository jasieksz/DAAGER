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

import java.util.function.Function;

import javaslang.Function2;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Seq;

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
	 * @return a new Pipeline for new population
	 */
	public Pipeline reproduce(final Function<Tuple2<EmasAgent, EmasAgent>, Seq<EmasAgent>> reproductionStrategy) {
		final List<EmasAgent> ts = pairs.flatMap(reproductionStrategy).distinct();
		return new Pipeline(ts);
	}

	public Pipeline reproduce(final Function2<EmasAgent, EmasAgent, Seq<EmasAgent>> reproductionStrategy) {
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
}
