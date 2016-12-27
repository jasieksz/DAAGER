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

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.emas.reproduction.improvement.Improvement;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

/**
 * Builder for building {@link SexualReproduction} functions. Built operators are based on {@link
 * SexualReproductionPipeline}.
 *
 * When building a sexual reproduction operator at least:
 * * recombination,
 * * energy transfer
 * need to be provided.
 *
 * @param <S>
 * 		type of the solution
 */
@SuppressWarnings({"ParameterHidesMemberVariable", "InstanceVariableMayNotBeInitialized"})
public final class SexualReproductionBuilder<S extends Solution<?>> {

	private Recombination<S> recombination;

	private Mutation<S> mutation;

	private Improvement<S> improvement;

	private EnergyTransfer energyTransfer;

	private Evaluator<S> evaluator;

	SexualReproductionBuilder() {}

	public SexualReproductionBuilder<S> withRecombination(final Recombination<S> recombination) {
		this.recombination = requireNonNull(recombination);
		return this;
	}

	public SexualReproductionBuilder<S> withMutation(final Mutation<S> mutation) {
		this.mutation = requireNonNull(mutation);
		return this;
	}

	public SexualReproductionBuilder<S> withImprovement(final Improvement<S> improvement) {
		this.improvement = requireNonNull(improvement);
		return this;
	}

	public SexualReproductionBuilder<S> withEnergyTransfer(final EnergyTransfer energyTransfer) {
		this.energyTransfer = requireNonNull(energyTransfer);
		return this;
	}

	public SexualReproductionBuilder<S> withEvaluator(final Evaluator<S> evaluator) {
		this.evaluator = requireNonNull(evaluator);
		return this;
	}


	public SexualReproduction build() {
		// Recombination is always required
		checkState(recombination != null);
		// Energy transfer is always required (to create a new child)
		checkState(energyTransfer != null);
		// Evaluator is always required
		checkState(evaluator != null);

		return parents -> {
			SexualReproductionPipeline<S> pipeline = SexualReproductionPipeline.on(parents._1, parents._2);
			pipeline = pipeline.recombine(recombination);
			if (mutation != null) {
				pipeline = pipeline.mutate(mutation);
			}
			pipeline = pipeline.evaluate(evaluator);
			if (improvement != null) {
				pipeline = pipeline.improve(improvement);
			}
			pipeline = pipeline.transferEnergy(energyTransfer);
			return pipeline.extract();
		};
	}
}
