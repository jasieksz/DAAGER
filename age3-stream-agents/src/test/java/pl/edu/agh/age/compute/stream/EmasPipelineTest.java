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

package pl.edu.agh.age.compute.stream;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Generators;
import pl.edu.agh.age.compute.stream.emas.Pipeline;
import pl.edu.agh.age.compute.stream.emas.Predicates;
import pl.edu.agh.age.compute.stream.emas.Selectors;
import pl.edu.agh.age.compute.stream.emas.solution.Solutions;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaslang.Tuple2;
import javaslang.collection.List;

public final class EmasPipelineTest {
	private static final Logger logger = LoggerFactory.getLogger(EmasPipelineTest.class);

	@Test public void testPipeline() {
		final List<EmasAgent> population = Generators.randomAgents(10);

		logger.info("Population before processing [{}]: {}", population.size(), population);
		final Pipeline pipeline = Pipeline.on(population)
		                                  .selectPairs(Selectors.random())
		                                  .reproduce(pair -> List.of(pair._1, pair._2,
		                                                             EmasAgent.create(1.0, Solutions.simple(2))))
		                                  .selectPairs(Selectors.random())
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
