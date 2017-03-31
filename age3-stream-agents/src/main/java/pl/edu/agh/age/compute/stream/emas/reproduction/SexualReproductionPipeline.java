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
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import org.checkerframework.checker.nullness.qual.Nullable;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Seq;

/**
 * Pipeline for processing parent agents in standard way.
 *
 * Supported operations:
 * * recombination,
 * * mutation,
 * * improvement,
 * * energy transfer,
 * * evaluation.
 *
 * Pipeline is immutable, new instance is created with each call.
 *
 * @param <S>
 * 		type of the solution.
 */
public final class SexualReproductionPipeline<S extends Solution<?>> {

	private final EmasAgent firstParent;

	private final EmasAgent secondParent;

	private final S firstSolution;

	private final S secondSolution;

	private final @Nullable EmasAgent child;

	private final @Nullable S childSolution;

	private SexualReproductionPipeline(final EmasAgent firstParent, final EmasAgent secondParent) {
		this(firstParent, secondParent, null, null);
	}

	private SexualReproductionPipeline(final EmasAgent firstParent, final EmasAgent secondParent,
	                                   final S childSolution) {
		this(firstParent, secondParent, null, childSolution);
	}

	@SuppressWarnings("unchecked")
	private SexualReproductionPipeline(final EmasAgent firstParent, final EmasAgent secondParent,
	                                   final EmasAgent child) {
		this(firstParent, secondParent, child, (S)child.solution);
	}

	@SuppressWarnings("unchecked")
	private SexualReproductionPipeline(final EmasAgent firstParent, final EmasAgent secondParent,
	                                   final @Nullable EmasAgent child, final @Nullable S childSolution) {
		this.firstParent = requireNonNull(firstParent);
		this.secondParent = requireNonNull(secondParent);
		this.child = child;
		this.childSolution = childSolution;
		firstSolution = (S)firstParent.solution;
		secondSolution = (S)secondParent.solution;
	}

	/**
	 * Create a pipeline for two given parents.
	 *
	 * @param firstParent
	 * @param secondParent
	 * @param <S>
	 * 		type of the solution
	 */
	public static <S extends Solution<?>> SexualReproductionPipeline<S> on(final EmasAgent firstParent,
	                                                                       final EmasAgent secondParent) {
		return new SexualReproductionPipeline<>(firstParent, secondParent);
	}

	public SexualReproductionPipeline<S> recombine(final Recombination<S> recombination) {
		return new SexualReproductionPipeline<>(firstParent, secondParent,
		                                        recombination.recombine(firstSolution, secondSolution));
	}

	public SexualReproductionPipeline<S> mutate(final Mutation<S> mutation) {
		return new SexualReproductionPipeline<>(firstParent, secondParent, mutation.mutate(childSolution));
	}

	public SexualReproductionPipeline<S> transferEnergy(final EnergyTransfer energyTransfer) {
		final double[] energyValue = energyTransfer.transfer(firstParent, secondParent);

		final EmasAgent newChild = (child != null)
		                           ? child.withEnergy(energyValue[2])
		                           : EmasAgent.create(energyValue[2], childSolution);

		return new SexualReproductionPipeline<>(firstParent.withEnergy(energyValue[0]),
		                                        secondParent.withEnergy(energyValue[1]), newChild);
	}

	/**
	 * Extracts agents from this pipeline.
	 *
	 * @return a tuple of agents: two new parents and a child.
	 *
	 * @throws IllegalStateException
	 * 		if the child was not generated (usually `transferEnergy` was not called).
	 */
	public Tuple2<Seq<EmasAgent>, EmasAgent> extract() {
		checkState(child != null, "The child was not created. You must call transferEnergy before extraction.");

		return Tuple.of(List.of(firstParent, secondParent), child);
	}

}

