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
package pl.edu.agh.age.console.command;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.client.TopologyServiceClient;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting and configuring topology of the cluster.
 */
@Named
public final class TopologyCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(TopologyCommand.class);

	private final TopologyServiceClient topologyService;

	private final PrintWriter writer;

	@Inject public TopologyCommand(final TopologyServiceClient topologyService, final Terminal terminal) {
		this.topologyService = requireNonNull(topologyService);
		writer = new PrintWriter(terminal.writer(), true);
	}

	@Override public String name() {
		return "topology";
	}

	@Operation(description = "Prints information about topology.") public void info() {
		final Optional<String> masterId = topologyService.masterId();
		final Optional<DirectedGraph<String, DefaultEdge>> topology = topologyService.topologyGraph();
		final Optional<String> topologyType = topologyService.topologyType();

		writer.println("Topology info = {");
		writer.println("\tmaster = " + masterId.orElse("# not elected #"));
		if (topology.isPresent()) {
			writer.println("\ttopology type = " + topologyType.get());
			writer.println("\ttopology = {");
			for (final DefaultEdge edge : topology.get().edgeSet()) {
				writer.println("\t\t" + edge);
			}
			writer.println("\t}");
		} else {
			writer.println("\ttopology type = # no topology #");
		}
		writer.println("}");
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
