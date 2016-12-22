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

import javaslang.collection.Map;
import javaslang.collection.Set;

/**
 * Interface for workplaces to interact with the global state of computation.
 *
 * @implSpec implementations must be aware that this can be used from multiple threads
 */
public interface Manager {
	/**
	 * Posts provided statistics globally, mapping them to the workplace ID.
	 *
	 * @param id
	 * 		workplace id
	 * @param statistics
	 * 		statistics as a map
	 */
	void postStatistics(long id, Map<Object, Object> statistics);

	/**
	 * Returns a read-only view of global statistics map.
	 */
	Map<Long, Map<Object, Object>> getStatistics();

	/**
	 * Returns a read-only view of global statistics map filtered only for neighbours of the given workplace.
	 */
	Map<Long, Map<Object, Object>> getNeighboursStatistics(long workplaceId);

	/**
	 * Returns a read-only view of global statistics map filtered only for neighbours of the given workplace.
	 */
	Map<Long, Set<String>> getNeighboursOf(long workplaceId);

	/**
	 * Migrates an agent to a workplace with the given ID in neighbourhood.
	 *
	 * This method always use the current topology.
	 */
	void migrate(Agent agent, long sourceWorkplace, long targetWorkplace);

	/**
	 * Migrates an agent to a workplace with the given annotation in neighbourhood.
	 *
	 * This method always use the current topology.
	 */
	void migrate(Agent agent, long sourceWorkplace, String neighbourAnnotation);

	/**
	 * Migrates an agent to a workplace with the given ID.
	 *
	 * This method makes it possible to bypass a current topology.
	 */
	void migrateUnconditionally(Agent agent, long sourceWorkplace, long targetWorkplace);

	boolean isStopConditionReached();
}
