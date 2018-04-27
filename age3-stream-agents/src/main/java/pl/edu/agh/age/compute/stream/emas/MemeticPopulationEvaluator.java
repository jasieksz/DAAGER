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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.PopulationEvaluator;
import pl.edu.agh.age.compute.stream.emas.reproduction.improvement.Improvement;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

import io.vavr.collection.Seq;

public final class MemeticPopulationEvaluator<S extends Solution<?>> implements PopulationEvaluator<EmasAgent> {

	private final Evaluator<S> evaluator;

	private final @Nullable Improvement<S> improvement;

	public MemeticPopulationEvaluator(final Evaluator<S> evaluator, final Improvement<S> improvement) {
		this.evaluator = requireNonNull(evaluator, "Evaluator has not been defined");
		this.improvement = improvement;
	}

	@SuppressWarnings("unchecked")
	@Override public Seq<EmasAgent> evaluate(final Seq<EmasAgent> population) {
		return population.map(agent -> {
			final S solution = (S)agent.solution;
			solution.withFitness(evaluator.evaluate(solution));
			return EmasAgent.create(agent.energy, (improvement != null) ? improvement.improve(solution) : solution);
		});
	}

}
