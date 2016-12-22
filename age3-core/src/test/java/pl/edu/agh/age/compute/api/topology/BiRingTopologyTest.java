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

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.services.worker.internal.topology.AnnotatedEdge;

import com.google.common.collect.ImmutableSet;

import org.jgrapht.DirectedGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public final class BiRingTopologyTest {

	private static final String[] annotations = {"left", "right"};

	private BiRingTopology<Integer> topology = null;

	@Before public void setUp() {
		topology = new BiRingTopology<>();
	}

	@Test public void test() {
		final Set<Integer> ids = ImmutableSet.of(10, 1, 3, 5, 12, 43, 6);
		final DirectedGraph<Integer, AnnotatedEdge> graph = topology.apply(ids);

		assertThat(ids).describedAs("all vertices satisfy").allSatisfy(i -> {
			assertThat(graph.outDegreeOf(i)).describedAs("out degree of vertex is 2").isEqualTo(2);
			assertThat(graph.inDegreeOf(i)).describedAs("in degree of vertex is 2").isEqualTo(2);

			assertThat(graph.outgoingEdgesOf(i)).describedAs("all outgoing edges satisfy")
			                                    .allSatisfy(e -> assertThat(e.annotations()).describedAs(
				                                    "annotations within range").isSubsetOf(annotations));

			assertThat(graph.incomingEdgesOf(i)).describedAs("all incoming edges satisfy")
			                                    .allSatisfy(e -> assertThat(e.annotations()).describedAs(
				                                    "annotations within range").isSubsetOf(annotations));
		});
	}

	@Test public void testEmpty() {
		final Set<Integer> ids = ImmutableSet.of();
		final DirectedGraph<Integer, AnnotatedEdge> graph = topology.apply(ids);

		assertThat(graph.vertexSet()).describedAs("no vertices").isEmpty();
		assertThat(graph.edgeSet()).describedAs("no edges").isEmpty();
	}

	@Test public void testSingleVertex() {
		final Set<Integer> ids = ImmutableSet.of(1);
		final DirectedGraph<Integer, AnnotatedEdge> graph = topology.apply(ids);

		assertThat(graph.vertexSet()).describedAs("one vertex").hasSize(1);
		assertThat(graph.edgeSet()).describedAs("one tight loop edge")
		                           .hasSize(1)
		                           .flatExtracting(AnnotatedEdge::annotations)
		                           .containsExactlyInAnyOrder(annotations);
	}
}
