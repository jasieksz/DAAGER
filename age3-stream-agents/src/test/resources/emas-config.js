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

// Load default EMAS extensions
load('classpath:emas.js');

var step = function (population, environment) {
	return Pipeline.on(population)
	               .selectPairs(Selectors.random())
	               .reproduce(function (pair) {
		               return List.of(pair._1, pair._2, EmasAgent.create(0.9, Solutions.simple(2)));
	               })
	               .selectPairs(Selectors.random())
	               .fight(function (pair) {
		               return List.of(pair._1, pair._2);
	               })
	               .migrateWhen(Predicates.random(0.2))
	               ._2()
	               .dieWhen(function (agent) { return agent.energy < 0.1; })
	               ._2()
	               .extract();
};

var afterStep = function (population) {
	return HashMap.of(StatisticsKeys.ENERGY_SUM, population.map(function (agent) {
		return emasAgent.energy;
	}).sum());
};

// Only these two variables must be defined, as the loader is looking for them

var stopCondition = new TimedStopCondition(10);

var workplaces = [
	{agents: Generators.randomAgents(5), step: step, after: afterStep},
	{agents: Generators.randomAgents(5), step: step, after: afterStep},
];
