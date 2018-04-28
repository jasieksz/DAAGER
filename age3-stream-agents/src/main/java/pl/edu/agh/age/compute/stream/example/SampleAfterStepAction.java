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

package pl.edu.agh.age.compute.stream.example;

import pl.edu.agh.age.compute.stream.AfterStepAction;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.StatisticsKeys;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

public final class SampleAfterStepAction implements AfterStepAction<EmasAgent, StatisticsKeys> {

	private static final long serialVersionUID = -5852903273256254808L;

	@Override
	public Map<StatisticsKeys, Object> apply(final Long workplaceId, final Long step,
	                                         final List<EmasAgent> population) {
		return HashMap.of(StatisticsKeys.STEP_NUMBER, step,
			              StatisticsKeys.ENERGY_SUM, sumEnergy(population),
		                  StatisticsKeys.AVERAGE_FITNESS, computeAverageFitness(population),
		                  StatisticsKeys.POPULATION_SIZE, population.size());
	}


	// FIXME: Utility class?
	private static double computeAverageFitness(final Seq<EmasAgent> population) {
		return population.map(agent -> agent.solution.fitnessValue()).average().getOrElse(0.0);
	}

	// FIXME: Utility class?
	private static double sumEnergy(final Seq<EmasAgent> population) {
		return population.map(emasAgent -> emasAgent.energy).sum().doubleValue();
	}
}
