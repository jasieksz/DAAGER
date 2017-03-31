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

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.AsexualEnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import org.checkerframework.checker.nullness.qual.Nullable;

import javaslang.Tuple;
import javaslang.Tuple2;

/**
 * Pipeline for processing parent agents in standard way.
 *
 * Supported operations:
 * * mutation,
 * * evaluation,
 * * energy transfer.
 *
 * Pipeline is immutable, new instance is created with each call.
 *
 * @param <S>
 * 		type of the solution.
 */
public final class AsexualReproductionPipeline<S extends Solution<?>> {

	private final EmasAgent parent;

	private final @Nullable EmasAgent child;

	private final @Nullable S childSolution;

	private AsexualReproductionPipeline(final EmasAgent parent) {
		this(parent, null, null);
	}

	private AsexualReproductionPipeline(final EmasAgent parent, final S childSolution) {
		this(parent, null, childSolution);
	}

	@SuppressWarnings("unchecked")
	private AsexualReproductionPipeline(final EmasAgent parent, final EmasAgent child) {
		this(parent, child, (S)child.solution);
	}

	private AsexualReproductionPipeline(final EmasAgent parent, final @Nullable EmasAgent child,
	                                    final @Nullable S childSolution) {
		this.parent = requireNonNull(parent);
		this.child = child;
		this.childSolution = childSolution;
	}

	/**
	 * Create a pipeline for one parent.
	 *
	 * @param parent
	 * @param <S>
	 * 		type of the solution
	 */
	public static <S extends Solution<?>> AsexualReproductionPipeline<S> on(final EmasAgent parent) {
		return new AsexualReproductionPipeline<>(parent);
	}

	public AsexualReproductionPipeline<S> recombine() {
		return new AsexualReproductionPipeline<>(parent, parent.withEnergy(0.0));
	}

	public AsexualReproductionPipeline<S> mutate(final Mutation<S> mutation) {
		return new AsexualReproductionPipeline<>(parent, mutation.mutate(childSolution));
	}

	public AsexualReproductionPipeline<S> transferEnergy(final AsexualEnergyTransfer energyTransfer) {
		final double[] energyValue = energyTransfer.transfer(parent);

		final EmasAgent newChild = (child != null)
		                           ? child.withEnergy(energyValue[1])
		                           : EmasAgent.create(energyValue[1], childSolution);

		return new AsexualReproductionPipeline<>(parent.withEnergy(energyValue[0]), newChild);
	}

	@SuppressWarnings("unchecked") public AsexualReproductionPipeline<S> evaluate(final Evaluator<S> evaluator) {
		checkState(childSolution != null, "Evaluation requires child solution");

		final S evaluatedChild = (S)childSolution.updateFitness(evaluator.evaluate(childSolution));
		return new AsexualReproductionPipeline<>(parent, evaluatedChild);
	}

	/**
	 * Extracts agents from this pipeline.
	 *
	 * @return a tuple of agents: a parent and a child.
	 *
	 * @throws IllegalStateException
	 * 		if the child was not generated (usually `transferEnergy` was not called).
	 */
	public Tuple2<EmasAgent, EmasAgent> extract() {
		checkState(child != null, "The child was not created. You must call transferEnergy before extraction.");

		// Sanity check. Although evaluation is not required by API it is usually expected
		// Not a checkState() because of possible performance issues
		assert !Double.isNaN(child.solution.fitnessValue());

		return Tuple.of(parent, child);
	}

}

