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

package pl.edu.agh.age.services.topology.processors;

import pl.edu.agh.age.services.identity.NodeDescriptor;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

/**
 * Topology processor generates a topology graph from the given set of nodes.
 */
@FunctionalInterface
public interface TopologyProcessor {

	/**
	 * Returns a priority of a processor (higher is more important).
	 *
	 * Used for selecting the initial processor.
	 *
	 * By default returns 0.
	 */
	default int priority() {
		return 0;
	}

	/**
	 * Return a name of the processor.
	 *
	 * By default returns an empty string.
	 */
	default String name() {
		return "";
	}

	/**
	 * Returns a graph of node connections based on the given set of nodes.
	 *
	 * @param identities
	 * 		node identities.
	 *
	 * @return a directed graph of node connections.
	 */
	Graph<String, DefaultEdge> createGraphFrom(Set<? extends NodeDescriptor> identities);
}
