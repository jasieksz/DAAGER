/*
 * Copyright (C) 2016-2017 Intelligent Information Systems Group.
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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import javaslang.collection.Seq;

/**
 * PopulationEvaluator is a function that computes fitness and optionally improves all of the
 * individuals in a given population.
 *
 * @param <A>
 *        the type an individual agent
 */
@FunctionalInterface
public interface PopulationEvaluator<A extends Agent> {

	Seq<EmasAgent> evaluate(Seq<EmasAgent> population);

	@SuppressWarnings("unchecked")
	static <S extends Solution<?>> PopulationEvaluator<EmasAgent> simpleEvaluator(final Evaluator<S> evaluator) {
		requireNonNull(evaluator, "Evaluator has not been defined");
		return (population) -> {
			return population.map(agent -> {
				final S solution = (S)agent.solution;
				solution.updateFitness(evaluator.evaluate(solution));
				return EmasAgent.create(agent.energy, solution);
			});
		};
	}

}
