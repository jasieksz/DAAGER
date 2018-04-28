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

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.ea.solution.SimpleSolution;
import pl.edu.agh.age.compute.ea.solution.Solutions;

import org.junit.Before;
import org.junit.Test;

import io.vavr.Tuple;
import io.vavr.collection.List;

public final class GeneticPipelineTest {

	private List<SimpleSolution<Integer>> population;

	private GeneticPipeline<Integer, SimpleSolution<Integer>> pipeline;

	@Before public void setUp() {
		population = Populations.createPopulation(10, Solutions::simple);
		pipeline = GeneticPipeline.on(population);
	}

	@Test public void preselect() {
		final List<SimpleSolution<Integer>> list = pipeline.preselect(pop -> pop.filter(s -> s.unwrap() > 5)).extract();
		assertThat(list).allMatch(s -> s.unwrap() > 5);
	}

	@Test public void pairedRecombineAll() {
		final List<SimpleSolution<Integer>> list = pipeline.pairedRecombine(
			(s1, s2) -> Tuple.of(s1.cloneWithNewValue(s2.unwrap()), s2.cloneWithNewValue(s1.unwrap())), 1.0).extract();

		assertThat(list).hasSize(10);
		final List<Integer> integers = list.map(SimpleSolution::unwrap);
		assertThat(integers).containsExactly(1, 0, 3, 2, 5, 4, 7, 6, 9, 8);
	}


	@Test public void pairedRecombineWithOddPopulation() {
		final List<SimpleSolution<Integer>> population = Populations.createPopulation(9, Solutions::simple);
		final GeneticPipeline<Integer, SimpleSolution<Integer>> pipeline = GeneticPipeline.on(population);
		final List<SimpleSolution<Integer>> list = pipeline.pairedRecombine(
			(s1, s2) -> Tuple.of(s1.cloneWithNewValue(s2.unwrap()), s2.cloneWithNewValue(s1.unwrap())), 1.0).extract();

		assertThat(list).hasSize(9);
		final List<Integer> integers = list.map(SimpleSolution::unwrap);
		assertThat(integers).containsExactly(1, 0, 3, 2, 5, 4, 7, 6, 8);
	}

	@Test public void pairedRecombineNone() {
		final List<SimpleSolution<Integer>> list = pipeline.pairedRecombine(
			(s1, s2) -> Tuple.of(s1.cloneWithNewValue(s2.unwrap()), s2.cloneWithNewValue(s1.unwrap())), 0.0).extract();

		assertThat(list).hasSize(10);
		final List<Integer> integers = list.map(SimpleSolution::unwrap);
		assertThat(integers).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test public void individualMutationAll() {
		final List<SimpleSolution<Integer>> list = pipeline.individualMutation(solution -> new SimpleSolution<>(0), 1.0)
		                                                   .extract();

		assertThat(list).hasSize(10);
		final List<Integer> integers = list.map(SimpleSolution::unwrap);
		assertThat(integers).containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Test public void individualMutationNone() {
		final List<SimpleSolution<Integer>> list = pipeline.individualMutation(solution -> new SimpleSolution<>(0), 0.0)
		                                                   .extract();

		assertThat(list).hasSize(10);
		final List<Integer> integers = list.map(SimpleSolution::unwrap);
		assertThat(integers).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}


	@Test public void evaluate() {
		final List<SimpleSolution<Integer>> list = pipeline.evaluate(solution -> solution.unwrap().doubleValue())
		                                                   .extract();

		assertThat(list).hasSize(10);
		final List<Double> evaluations = list.map(SimpleSolution::evaluationValue);
		assertThat(evaluations).containsExactly(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);
	}

}
