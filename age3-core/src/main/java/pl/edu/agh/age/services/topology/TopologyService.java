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
package pl.edu.agh.age.services.topology;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Optional;
import java.util.Set;

public interface TopologyService {

	/**
	 * Returns a set of neighbours of the current node.
	 *
	 * @return set of {@link String}, possibly empty when there is no topology or the node has no neighbours.
	 *
	 * @throws IllegalStateException
	 * 		when the topology cannot get the list of neighbours because it has not finished discovery.
	 */
	Set<String> neighbours();

	/**
	 * Returns the current topology graph.
	 *
	 * @return an Optional containing the topology graph or empty when no topology was set.
	 */
	Optional<Graph<String, DefaultEdge>> topologyGraph();

	Optional<String> topologyType();

	Optional<String> masterId();

	boolean isLocalNodeMaster();

	boolean hasTopology();
}
