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

package pl.edu.agh.age.compute.api;

import pl.edu.agh.age.compute.api.topology.Topology;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Provides a topology service for the compute level.
 *
 * Topologies, beside defining "neighbours" for each node will provide annotations for each one of them. They may
 * describe, for example, an abstract direction of the neighbour.
 *
 * Neighbourhood may be directional. It means that *2* being a neighbour of *1* does not mean that *1* is a neighbour of
 * *2*.
 *
 * @param <T>
 * 		type of the objects in the topology. Usually some kind of an identifier.
 */
public interface TopologyProvider<T extends Serializable> {

	/**
	 * Set the new topology function.
	 *
	 * @param topology
	 * 		a function used to generate topologies
	 */
	void setTopology(final Topology<T> topology);

	/**
	 * Add nodes to the current topology.
	 *
	 * @param ids
	 * 		IDs of the nodes to add
	 */
	void addNodes(Set<T> ids);

	/**
	 * Returns neighbours (according to the current topology).
	 *
	 * @param id
	 * 		id of the workplace to look up neighbours
	 *
	 * @return a map where keys are neighbours IDs (passed using {@code #addNodes}) and values are sets of annotations
	 * assigned by the topology function to this neighbour
	 */
	Map<T, Set<String>> neighboursOf(T id);

	/**
	 * Returns neighbours (according to the current topology).
	 *
	 * Warning: values may be duplicated if single neighbour has multiple annotations on its edges.
	 *
	 * @param id
	 * 		id of the workplace to look up neighbours
	 *
	 * @return a map where keys are annotations assigned by the topology function and values are IDs passed using {@code
	 * #addNodes}
	 */
	Map<String, T> neighboursOfByAnnotation(T id);

	/**
	 * Check if the `second` is a neighbour of `first` in this exact direction
	 *
	 * @param first
	 * 		first id
	 * @param second
	 * 		second it
	 *
	 * @return true if and only if second is neighbour of first
	 */
	boolean areNeighbours(T first, T second);
}
