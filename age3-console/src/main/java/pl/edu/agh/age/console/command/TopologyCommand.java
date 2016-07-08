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

import pl.edu.agh.age.client.TopologyServiceClient;

import com.beust.jcommander.Parameters;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting and configuring topology of the cluster.
 */
@Named
@Parameters(commandNames = "topology", commandDescription = "Topology management", optionPrefixes = "--")
public final class TopologyCommand extends BaseCommand {

	private enum Operation {
		INFO("info");

		private final String operationName;

		Operation(final String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(TopologyCommand.class);

	@Inject private TopologyServiceClient topologyService;

	public TopologyCommand() {
		addHandler(Operation.INFO.operationName(), this::info);
	}

	@Override public final Set<String> operations() {
		return Arrays.stream(Operation.values()).map(Operation::operationName).collect(Collectors.toSet());
	}

	private void info(final Terminal printWriter) {
		final Optional<String> masterId = topologyService.masterId();
		final Optional<DirectedGraph<String, DefaultEdge>> topology = topologyService.topologyGraph();
		final Optional<String> topologyType = topologyService.topologyType();

		printWriter.writer().println("Topology info = {");
		printWriter.writer().println("\tmaster = " + masterId.orElse("# not elected #"));
		if (topology.isPresent()) {
			printWriter.writer().println("\ttopology type = " + topologyType.get());
			printWriter.writer().println("\ttopology = {");
			for (final DefaultEdge edge : topology.get().edgeSet()) {
				printWriter.writer().println("\t\t" + edge);
			}
			printWriter.writer().println("\t}");
		} else {
			printWriter.writer().println("\ttopology type = # no topology #");
		}
		printWriter.writer().println("}");
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
