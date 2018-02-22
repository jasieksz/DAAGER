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

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.worker.internal.topology.AnnotatedEdge;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.Serializable;
import java.util.Set;

/**
 * Full mesh topology generator.
 *
 * It does not generate a tight loop for a single vertex.
 *
 * Sample topologies:
 * - for one vertex: no connections
 * - for two vertices: `1 <-> 2`
 * - for three vertices: `1 <-> 2 <-> 3 <-> 1`
 *
 * Annotation used by this processor: a decimal number from 0 to the number of neighbours.
 */
public final class FullMeshTopology<T extends Serializable> implements Topology<T> {

	@Override public Graph<T, AnnotatedEdge> apply(final Set<T> identities) {
		requireNonNull(identities);

		final DefaultDirectedGraph<T, AnnotatedEdge> graph = new DefaultDirectedGraph<>(AnnotatedEdge.class);
		identities.forEach(graph::addVertex);

		StreamEx.of(identities)
		        .forEach(v1 -> StreamEx.of(identities)
		                               .without(v1)
		                               .zipWith(IntStreamEx.ints().mapToObj(String::valueOf).map(AnnotatedEdge::new))
		                               .forEach(v2 -> graph.addEdge(v1, v2.getKey(), v2.getValue())));
		return new AsUnmodifiableGraph<>(graph);
	}

	@Override public String toString() {
		return name();
	}

	@Override public String name() {
		return "full-mesh";
	}
}
