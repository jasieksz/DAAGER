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

package pl.edu.agh.age.compute.stream.example;

import pl.edu.agh.age.compute.stream.Environment;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Pipeline;
import pl.edu.agh.age.compute.stream.emas.Predicates;
import pl.edu.agh.age.compute.stream.emas.Selectors;
import pl.edu.agh.age.compute.stream.emas.reproduction.SexualReproduction;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleSolution;

import javaslang.Tuple2;
import javaslang.collection.List;

/**
 * Sample step showing all operations.
 */
public final class SampleStep implements Step<EmasAgent> {

	@Override public List<EmasAgent> stepOn(final List<EmasAgent> population, final Environment environment) {
		final Recombination<DoubleSolution> recombination
			= (firstSolution, secondSolution) -> new DoubleSolution((firstSolution.value() * 0.1)
			                                                        + (secondSolution.value() * 0.9));
		final SexualReproduction reproduction = SexualReproduction.<DoubleSolution>builder().withRecombination(
			recombination).withEnergyTransfer(EnergyTransfer.equal()).withEvaluator(DoubleSolution::value).build();

		final Pipeline pipeline = Pipeline.on(population)
		                                  .selectPairsWithRepetitions(Selectors.random())
		                                  .reproduce(reproduction)
		                                  .selectPairsWithRepetitions(Selectors.random())
		                                  .fight(pair -> List.of(pair._1, pair._2));
		final Tuple2<Pipeline, Pipeline> afterMigration = pipeline.migrateWhen(Predicates.random(0.2));
		final List<EmasAgent> migrated = afterMigration._1.extract();

		migrated.forEach(emasAgent -> environment.migrate(emasAgent, 1));

		final Tuple2<Pipeline, Pipeline> afterDeath = afterMigration._2().dieWhen(agent -> agent.energy < 0.1);

		final List<EmasAgent> dead = afterDeath._1.extract();
		environment.logPopulation("dead", dead);
		return afterDeath._2().extract();
	}
}
