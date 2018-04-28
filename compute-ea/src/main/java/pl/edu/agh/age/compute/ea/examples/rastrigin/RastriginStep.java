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

package pl.edu.agh.age.compute.ea.examples.rastrigin;

import pl.edu.agh.age.compute.ea.GeneticPipeline;
import pl.edu.agh.age.compute.ea.Step;
import pl.edu.agh.age.compute.ea.evaluation.real.RastriginEvaluator;
import pl.edu.agh.age.compute.ea.preselection.RankPreselection;
import pl.edu.agh.age.compute.ea.preselection.StochasticPreselection;
import pl.edu.agh.age.compute.ea.rand.JavaRandomGenerator;
import pl.edu.agh.age.compute.ea.scaling.SimpleScaling;
import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.ea.variation.mutation.real.NormalMicroMacroMutate;
import pl.edu.agh.age.compute.ea.variation.recombination.real.AverageRecombine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

import io.vavr.collection.Array;
import io.vavr.collection.List;

public final class RastriginStep implements Step<DoubleVectorSolution> {

	private static final Logger logger = LoggerFactory.getLogger(RastriginStep.class);

	private final JavaRandomGenerator generator;

	private final double chanceToRecombine;

	private final double chanceToMutate;

	private final double mutationRange;

	@Inject
	public RastriginStep(final JavaRandomGenerator generator,
	                     final @Value("${individual.chanceToRecombine}") double chanceToRecombine,
	                     final @Value("${individual.chanceToMutate}") double chanceToMutate,
	                     final @Value("${feature.mutationRange}") double mutationRange) {
		this.generator = generator;
		this.chanceToRecombine = chanceToRecombine;
		this.chanceToMutate = chanceToMutate;
		this.mutationRange = mutationRange;

		logger.info("Parameters are: chanceToRecombine={}, chanceToMutate={}, mutationRange={}", chanceToRecombine,
		            chanceToMutate, mutationRange);
	}


	@Override
	public List<DoubleVectorSolution> stepOn(final long stepNumber, final List<DoubleVectorSolution> population) {
		final SimpleScaling scaling = new SimpleScaling();
		final StochasticPreselection<Double, DoubleVectorSolution> preselection = new StochasticPreselection<>(scaling,
		                                                                                                       generator);
		final RankPreselection<Array<Double>, DoubleVectorSolution> rankPreselection = new RankPreselection<>(
			preselection);
		final RastriginEvaluator evaluator = new RastriginEvaluator();

		GeneticPipeline<Array<Double>, DoubleVectorSolution> pipeline = GeneticPipeline.on(population);
		if (stepNumber == 1) {
			pipeline = pipeline.evaluate(evaluator);
		}

		return pipeline.preselect(rankPreselection)
		               .pairedRecombine(new AverageRecombine(), chanceToRecombine)
		               .individualMutation(new NormalMicroMacroMutate(generator, generator, mutationRange),
		                                   chanceToMutate)
		               .evaluate(evaluator)
		               .extract();
	}
}
