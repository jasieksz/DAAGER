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

package pl.edu.agh.age.compute.api.topology;

import pl.edu.agh.age.services.worker.internal.topology.AnnotatedEdge;

import org.jgrapht.DirectedGraph;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;

/**
 * Topology function builds a topology graph from the given set of elements.
 *
 * @param <T>
 * 		a type of elements in vertices of the topology graph
 */
@FunctionalInterface
public interface Topology<T extends Serializable> extends Function<Set<T>, DirectedGraph<T, AnnotatedEdge>> {

	/**
	 * Creates a new unidirectional ring topology.
	 *
	 * @see UniRingTopology
	 */
	static <R extends Serializable> Topology<R> unidirectionalRing() {
		return new UniRingTopology<>();
	}

	/**
	 * Creates a new bidirectional ring topology.
	 *
	 * @see BiRingTopology
	 */
	static <R extends Serializable> Topology<R> bidirectionalRing() {
		return new BiRingTopology<>();
	}

	/**
	 * Creates a new full mesh topology.
	 *
	 * @see FullMeshTopology
	 */
	static <R extends Serializable> Topology<R> fullMesh() {
		return new FullMeshTopology<>();
	}

	/**
	 * Returns the name of the topology.
	 */
	default String name() {
		return "";
	}
}
