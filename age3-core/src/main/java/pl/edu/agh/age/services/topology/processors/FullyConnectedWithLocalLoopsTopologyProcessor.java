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

package pl.edu.agh.age.services.topology.processors;

import static com.google.common.collect.Sets.cartesianProduct;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.identity.NodeDescriptor;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

import java.util.Set;

import javax.inject.Named;

@Named
public final class FullyConnectedWithLocalLoopsTopologyProcessor implements TopologyProcessor {

	private static final int PRIORITY = 50;

	@Override public int priority() {
		return PRIORITY;
	}

	@Override public String name() {
		return "fully connected with local loops";
	}

	@Override
	public DirectedGraph<String, DefaultEdge> createGraphFrom(final Set<? extends NodeDescriptor> identities) {
		requireNonNull(identities);

		final DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		identities.forEach(identity -> graph.addVertex(identity.id()));
		cartesianProduct(identities, identities).forEach(elem -> {
			final NodeDescriptor id1 = elem.get(0);
			final NodeDescriptor id2 = elem.get(1);
			graph.addEdge(id1.id(), id2.id());
		});
		return new UnmodifiableDirectedGraph<>(graph);
	}

	@Override public String toString() {
		return name();
	}
}
