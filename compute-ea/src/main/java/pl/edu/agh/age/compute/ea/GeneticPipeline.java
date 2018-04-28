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

package pl.edu.agh.age.compute.ea;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.evaluation.Evaluator;
import pl.edu.agh.age.compute.ea.preselection.Preselection;
import pl.edu.agh.age.compute.ea.rand.JavaRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.Solution;
import pl.edu.agh.age.compute.ea.variation.mutation.Mutation;
import pl.edu.agh.age.compute.ea.variation.recombination.Recombination;

import java.io.Serializable;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public final class GeneticPipeline<T extends Serializable, S extends Solution<T>>
	extends Pipeline<S, GeneticPipeline<T, S>> {

	private final NormalizedDoubleRandomGenerator rand;

	private final Stream<Double> randomDoubles;

	private GeneticPipeline(final List<S> population, final NormalizedDoubleRandomGenerator rand) {
		super(population, x -> new GeneticPipeline<>((List<S>)x, rand));
		this.rand = requireNonNull(rand);
		randomDoubles = Stream.continually(rand::nextDouble);
	}

	private GeneticPipeline(final List<S> population) {
		this(population, new JavaRandomGenerator());
	}

	/**
	 * Create a new pipeline for the given population.
	 *
	 * @param population
	 * 		population of EMAS agents.
	 *
	 * @return a new pipeline instance.
	 */
	public static <I extends Serializable, V extends Solution<I>> GeneticPipeline<I, V> on(final List<V> population) {
		return new GeneticPipeline<>(population);
	}

	/**
	 * Changes the random number generator for the current pipeline.
	 *
	 * @param rand
	 * 		a new double random generator to use.
	 *
	 * @return a new pipeline instance with the new generator.
	 */
	public GeneticPipeline<T, S> withRandomGenerator(final NormalizedDoubleRandomGenerator rand) {
		return new GeneticPipeline<>(population, rand);
	}

	// Genetic operators

	/**
	 * Preselect the population using the given operator.
	 *
	 * @param preselection
	 * 		preselection operator.
	 *
	 * @return a pipeline instance with the preselected population.
	 */
	public GeneticPipeline<T, S> preselect(final Preselection<S> preselection) {
		return pipelineFactory.apply(preselection.preselect(population));
	}

	/**
	 * Recombine the population using the given operator.
	 * Recombination is done on two solutions located one after another on the list.
	 *
	 * @param recombination
	 * 		recombination operator.
	 * @param chanceToRecombine
	 * 		probability of two solutions to recombine.
	 *
	 * @return a pipeline instance with the new (recombined) population.
	 */
	@SuppressWarnings("unchecked") public GeneticPipeline<T, S> pairedRecombine(final Recombination<S> recombination,
	                                                                            final double chanceToRecombine) {
		final Function1<Tuple2<List<S>, Double>, Seq<S>> map = t -> ((t._1.length() > 1) && (t._2 < chanceToRecombine))
		                                                            ? (Seq<S>)recombination.recombine(t._1.get(0),
		                                                                                              t._1.get(1))
		                                                                                   .toSeq()
		                                                            : t._1;

		final List<S> newPopulation = population.grouped(2).zip(randomDoubles).flatMap(map).toList();
		return pipelineFactory.apply(newPopulation);
	}

	/**
	 * Individually mutate each solution with the given chance to mutate.
	 *
	 * @param mutation
	 * 		mutation operator.
	 * @param chanceToMutate
	 * 		probability that the solution will be mutated. Should be within [0,1] range.
	 *
	 * @return a pipeline instance with the new (mutated) population.
	 */
	public GeneticPipeline<T, S> individualMutation(final Mutation<S> mutation, final double chanceToMutate) {
		final List<S> newPopulation = population.zip(randomDoubles)
		                                        .map(t -> (t._2 < chanceToMutate) ? mutation.mutate(t._1) : t._1);
		return pipelineFactory.apply(newPopulation);
	}

	/**
	 * Evaluate the population.
	 *
	 * @param evaluator
	 * 		evaluation operator.
	 *
	 * @return a pipeline instance with the evaluated population.
	 */
	@SuppressWarnings("unchecked") public GeneticPipeline<T, S> evaluate(final Evaluator<S> evaluator) {
		final List<S> newPopulation = population.map(s -> (S)s.withEvaluation(evaluator.evaluate(s)));
		return pipelineFactory.apply(newPopulation);
	}
}
