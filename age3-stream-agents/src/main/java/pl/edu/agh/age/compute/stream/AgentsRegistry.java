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

package pl.edu.agh.age.compute.stream;

import java.util.OptionalDouble;

import io.vavr.Tuple3;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

/**
 * Registry of the best agents.
 *
 * @param <T>
 * 		type of agents stored in the registry
 */
public interface AgentsRegistry<T extends Agent> {

	/**
	 * Extracts and registers currently best agents from the population.
	 *
	 * @param workplaceId
	 * 		the workplace id
	 * @param stepNumber
	 * 		the step number
	 * @param population
	 * 		the current population
	 */
	void register(long workplaceId, long stepNumber, Seq<T> population);

	/**
	 * Gets the best agent evaluation value.
	 *
	 * @return the best agent evaluation value or `Optional#empty()`, if none available
	 */
	OptionalDouble getBestAgentEvaluation();

	/**
	 * Gets the best solution map.
	 *
	 * @return the immutable map of best solutions, in which keys are String representation of the best agents
	 * (or more formally their solutions) and values represent as follows: the workplace id (1)
	 * and step number (2) in which the agent was firstly found and total agent occurrences count (3)
	 * in all workplaces
	 */
	Map<String, Tuple3<Long, Long, Long>> getBestAgentsStatistics();

	static <T extends Agent> AgentsRegistry<T> empty() {
		return new AgentsRegistry<T>() {
			@Override public void register(final long workplaceId, final long stepNumber, final Seq<T> population) { }

			@Override public OptionalDouble getBestAgentEvaluation() { return OptionalDouble.of(0.0); }

			@Override public Map<String, Tuple3<Long, Long, Long>> getBestAgentsStatistics() { return HashMap.empty(); }
		};
	}

}
