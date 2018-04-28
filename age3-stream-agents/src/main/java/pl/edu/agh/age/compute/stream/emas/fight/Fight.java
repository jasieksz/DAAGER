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

package pl.edu.agh.age.compute.stream.emas.fight;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.function.Function;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;

/**
 * Fight is the function generating a list of new agents from the fighting tuple of a two.
 * The returned agent list will replace source agents in the population.
 */
@FunctionalInterface
public interface Fight extends Function<Tuple2<EmasAgent, EmasAgent>, Seq<EmasAgent>> {

	/**
	 * Creates a new builder for fight strategy.
	 *
	 * @param <S>
	 * 		Solution type
	 */
	static <S extends Solution<?>> FightBuilder<S> builder() {
		return new FightBuilder<>();
	}

}
