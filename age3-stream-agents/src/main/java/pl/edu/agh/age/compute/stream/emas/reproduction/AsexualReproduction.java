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

package pl.edu.agh.age.compute.stream.emas.reproduction;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.function.Function;

import io.vavr.Tuple2;

/**
 * Asexual reproduction is a function generating a new agent from a given one.
 * The returned tuple consist of 2 agents (a parent and a child).
 */
@FunctionalInterface
public interface AsexualReproduction extends Function<EmasAgent, Tuple2<EmasAgent, EmasAgent>> {

	/**
	 * Creates a new builder for asexual reproduction.
	 *
	 * @param <S>
	 * 		the solution type
	 */
	static <S extends Solution<?>> AsexualReproductionBuilder<S> builder() {
		return new AsexualReproductionBuilder<>();
	}
}
