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

package pl.edu.agh.age.compute.stream.emas.reproduction;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.function.Function;

import javaslang.Tuple2;
import javaslang.collection.Seq;

/**
 * Sexual reproduction is a function generating a list of new agents from two parents.
 *
 * It should include modified parents in the results.
 */
@FunctionalInterface
public interface SexualReproduction extends Function<Tuple2<EmasAgent, EmasAgent>, Tuple2<Seq<EmasAgent>, EmasAgent>> {

	/**
	 * Creates a new builder for sexual reproduction.
	 *
	 * @param <S>
	 * 		Solution type
	 */
	static <S extends Solution<?>> SexualReproductionBuilder<S> builder() {
		return new SexualReproductionBuilder<>();
	}
}
