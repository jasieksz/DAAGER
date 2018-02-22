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

import one.util.streamex.StreamEx;

import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Unidirectional ring topology generator.
 *
 * It does generate a tight loop for a single vertex.
 *
 * Sample topologies:
 * - for one vertex: `1 -> 1`
 * - for two vertices: `1 -> 2 -> 1`
 * - for three vertices: `1 -> 2 -> 3 -> 1`
 *
 * Annotation used by this processor:
 * - right
 */
public final class UniRingTopology<T extends Serializable> implements Topology<T> {

	@Override public Graph<T, AnnotatedEdge> apply(final Set<T> identities) {
		requireNonNull(identities);
		final DefaultDirectedGraph<T, AnnotatedEdge> graph = new DefaultDirectedGraph<>(AnnotatedEdge.class);

		if (identities.isEmpty()) {
			return new AsUnmodifiableGraph<>(graph);
		}

		identities.forEach(graph::addVertex);

		final List<T> sorted = StreamEx.of(identities).sorted(Comparator.comparingInt(Object::hashCode)).toList();
		StreamEx.of(sorted).forPairs((t1, t2) -> graph.addEdge(t1, t2, new AnnotatedEdge("right")));
		final T first = sorted.get(0);
		final T last = sorted.get(sorted.size() - 1);
		graph.addEdge(last, first, new AnnotatedEdge("right"));

		return new AsUnmodifiableGraph<>(graph);
	}

	@Override public String toString() {
		return name();
	}

	@Override public String name() {
		return "uni-ring";
	}
}
