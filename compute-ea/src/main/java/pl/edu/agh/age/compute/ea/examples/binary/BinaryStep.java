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

package pl.edu.agh.age.compute.ea.examples.binary;

import pl.edu.agh.age.compute.ea.GeneticPipeline;
import pl.edu.agh.age.compute.ea.Step;
import pl.edu.agh.age.compute.ea.evaluation.binary.M7Evaluator;
import pl.edu.agh.age.compute.ea.preselection.StochasticPreselection;
import pl.edu.agh.age.compute.ea.rand.JavaRandomGenerator;
import pl.edu.agh.age.compute.ea.scaling.SimpleScaling;
import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;
import pl.edu.agh.age.compute.ea.variation.mutation.binary.BinaryMutate;
import pl.edu.agh.age.compute.ea.variation.recombination.binary.BinaryOnePointRecombine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

import io.vavr.collection.Array;
import io.vavr.collection.List;

public final class BinaryStep implements Step<BooleanVectorSolution> {

	private static final Logger logger = LoggerFactory.getLogger(BinaryStep.class);

	private final JavaRandomGenerator generator;

	private final double chanceToRecombine;

	private final double chanceToMutate;

	@Inject
	public BinaryStep(final JavaRandomGenerator generator,
	                  final @Value("${individual.chanceToRecombine}") double chanceToRecombine,
	                  final @Value("${individual.chanceToMutate}") double chanceToMutate) {
		this.generator = generator;
		this.chanceToRecombine = chanceToRecombine;
		this.chanceToMutate = chanceToMutate;

		logger.info("Parameters are: chanceToRecombine={}, chanceToMutate={}", chanceToRecombine, chanceToMutate);
	}


	@Override
	public List<BooleanVectorSolution> stepOn(final long stepNumber, final List<BooleanVectorSolution> population) {
		final SimpleScaling scaling = new SimpleScaling();
		final M7Evaluator evaluator = new M7Evaluator();

		GeneticPipeline<Array<Boolean>, BooleanVectorSolution> pipeline = GeneticPipeline.on(population);
		if (stepNumber == 1) {
			pipeline = pipeline.evaluate(evaluator);
		}

		return pipeline.preselect(new StochasticPreselection<>(scaling, generator))
		               .pairedRecombine(new BinaryOnePointRecombine(generator), chanceToRecombine)
		               .individualMutation(new BinaryMutate(generator, generator), chanceToMutate)
		               .evaluate(evaluator)
		               .extract();
	}
}
