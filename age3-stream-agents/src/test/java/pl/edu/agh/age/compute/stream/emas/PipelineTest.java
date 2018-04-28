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

import pl.edu.agh.age.compute.stream.emas.solution.Solutions;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public final class PipelineTest {

	private static final Logger logger = LoggerFactory.getLogger(PipelineTest.class);

	@Rule public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

	@Test public void testPipeline() {
		final List<EmasAgent> population = Generators.randomAgents(10);

		logger.info("Population before processing [{}]: {}", population.size(), population);
		final Tuple2<Pipeline, Pipeline> reproduced = Pipeline.on(population) //
		                                                      .selectPairsWithRepetitions(PairSelectors.random()) //
		                                                      .reproduce(pair -> reproduce(pair));
		final Pipeline pipeline = reproduced._1.mergeWith(reproduced._2) //
		                                       .selectPairs(PairSelectors.random())._1 //
		                                       .fight(pair -> List.of(pair._1, pair._2));
		logger.info("Population after reproduction and fights [{}]: {}", pipeline.extract().size(), pipeline.extract());

		final Tuple2<Pipeline, Pipeline> split = pipeline.migrateWhen(Predicates.random(0.5));
		final Pipeline forMigration = split._1;
		final Pipeline staying = split._2;

		logger.info("Population split for migration [{}]: {}", forMigration.extract().size(), forMigration.extract());
		logger.info("Population split for staying [{}]: {}", staying.extract().size(), staying.extract());

		final Tuple2<Pipeline, Pipeline> deathSplit = staying.dieWhen(agent -> agent.energy < 0.1);

		final Pipeline dead = deathSplit._1;
		final Pipeline alive = deathSplit._2;

		logger.info("Population dead [{}]: {}", dead.extract().size(), dead.extract());
		logger.info("Population alive (new population) [{}]: {}", alive.extract().size(), alive.extract());
	}

	private Tuple2<Seq<EmasAgent>, EmasAgent> reproduce(final Tuple2<EmasAgent, EmasAgent> parents) {
		return Tuple.of(List.of(parents._1, parents._2), EmasAgent.create(1.0, Solutions.simple(2)));
	}

	@SuppressWarnings("unused")
	@Test public void testSelectPairs() {
		final List<EmasAgent> agents = Stream.range(0, 5)
		                                     .map(i -> EmasAgent.create(10, Solutions.simple(0)))
		                                     .appendAll(Stream.range(0, 4)
		                                                      .map(i -> EmasAgent.create(-10, Solutions.simple(0))))
		                                     .toList();
		// assert non-repetitions
		final java.util.List<EmasAgent> alreadyUsed = new ArrayList<>();
		final Tuple2<PairPipeline, Pipeline> tuple2 = Pipeline.on(agents).selectPairs((emasAgent, emasAgents) -> {
			final EmasAgent nextAgent = emasAgents.get();
			alreadyUsed.add(emasAgent);
			softly.assertThat(emasAgents).doesNotContainAnyElementsOf(alreadyUsed);
			alreadyUsed.add(nextAgent);
			return Tuple.of(emasAgent, nextAgent);
		});
		final PairPipeline pairPipeline = tuple2._1;
		final Pipeline pipeline = tuple2._2;

		softly.assertThat(pipeline.extract()).hasSize(1);
	}

	@SuppressWarnings("unused")
	@Test public void testSelectPairsWithRepetitions() {
		final List<EmasAgent> agents = Stream.range(0, 5).map(i -> EmasAgent.create(10, Solutions.simple(0))).toList();

		final PairPipeline pipeline = Pipeline.on(agents).selectPairsWithRepetitions((emasAgent, emasAgents) -> {
			softly.assertThat(emasAgent).isNotIn(emasAgents);
			softly.assertThat(emasAgents).isNotEmpty();
			return Tuple.of(emasAgent, emasAgents.get());
		});
	}

	/**
	 * This is the same test as {@link PipelineTest#testPipeline()} but with an empty population
	 */
	@Test public void testWorksWithEmptyPopulation() {
		final List<EmasAgent> population = List.empty();

		logger.info("Population before processing [{}]: {}", population.size(), population);
		final Tuple2<Pipeline, Pipeline> reproduced = Pipeline.on(population) //
                                                              .selectPairsWithRepetitions(PairSelectors.random()) //
                                                              .reproduce(pair -> reproduce(pair));
		final Pipeline pipeline = reproduced._1.mergeWith(reproduced._2)
			                                   .selectPairs(PairSelectors.random())._1 //
			                                   .fight(pair -> List.of(pair._1, pair._2));
		logger.info("Population after reproduction and fights [{}]: {}", pipeline.extract().size(), pipeline.extract());

		final Tuple2<Pipeline, Pipeline> split = pipeline.migrateWhen(Predicates.random(0.5));
		final Pipeline forMigration = split._1;
		final Pipeline staying = split._2;

		logger.info("Population split for migration [{}]: {}", forMigration.extract().size(), forMigration.extract());
		logger.info("Population split for staying [{}]: {}", staying.extract().size(), staying.extract());

		final Tuple2<Pipeline, Pipeline> deathSplit = staying.dieWhen(agent -> agent.energy < 0.1);

		final Pipeline dead = deathSplit._1;
		final Pipeline alive = deathSplit._2;

		logger.info("Population dead [{}]: {}", dead.extract().size(), dead.extract());
		logger.info("Population alive (new population) [{}]: {}", alive.extract().size(), alive.extract());
	}

}
