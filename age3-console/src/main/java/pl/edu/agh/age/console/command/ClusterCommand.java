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
package pl.edu.agh.age.console.command;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.console.command.Command.getAndCastDefault;
import static pl.edu.agh.age.console.command.Command.getAndCastNullable;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.client.StatusServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeType;
import pl.edu.agh.age.services.status.Status;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting info about and managing the whole cluster.
 */
@SuppressWarnings("unused")
@Named
public final class ClusterCommand implements Command {

	private static final Logger logger = LoggerFactory.getLogger(ClusterCommand.class);

	private final DiscoveryServiceClient discoveryService;

	private final LifecycleServiceClient lifecycleServiceClient;

	private final StatusServiceClient statusServiceClient;

	private final PrintWriter writer;

	@Inject
	public ClusterCommand(final DiscoveryServiceClient discoveryService,
	                      final LifecycleServiceClient lifecycleServiceClient,
	                      final StatusServiceClient statusServiceClient, final Terminal terminal) {
		this.discoveryService = requireNonNull(discoveryService);
		this.lifecycleServiceClient = requireNonNull(lifecycleServiceClient);
		this.statusServiceClient = requireNonNull(statusServiceClient);
		writer = new PrintWriter(terminal.writer(), true);
	}


	@Override public String name() {
		return "cluster";
	}

	/**
	 * Prints information about nodes (multiple or single).
	 */
	@Operation(description = "Prints information about nodes (multiple or single).")
	@Parameter(name = "id", type = Integer.class, optional = true, description = "id of node to print")
	@Parameter(name = "longOutput", type = Boolean.class, optional = true, description = "long mode")
	public void nodes(final Map<String, Object> parameters) {
		final Optional<Integer> id = getAndCastNullable(parameters, "id", Integer.class);
		final boolean longOutput = getAndCastDefault(parameters, "longOutput", Boolean.class, false);
		logger.debug("Printing information about nodes.");
		if (id.isPresent()) {
			final NodeDescriptor descriptor = discoveryService.memberWithId(id.get().toString()); // FIXME
			printNode(descriptor, longOutput);
		} else {
			final Set<NodeDescriptor> neighbours = discoveryService.allMembers();
			neighbours.forEach(descriptor -> printNode(descriptor, longOutput));
		}
	}

	public void nodes() {
		nodes(Collections.emptyMap());
	}

	/**
	 * Destroys the cluster.
	 */
	@Operation(description = "Destroys the cluster.") public void destroy() {
		lifecycleServiceClient.destroyCluster();
	}

	private void printNode(final NodeDescriptor descriptor, final boolean longOutput) {
		writer.println(String.format("%s - %s", descriptor.id(), descriptor.type()));
		if (longOutput && (descriptor.type() == NodeType.COMPUTE)) {
			final @Nullable Status status = statusServiceClient.getStatusForNode(descriptor);
			if (isNull(status)) {
				writer.println("\tNo status for the node. Seems to be an error.");
				return;
			}
			writer.println(
				"\tLast updated: " + status.creationTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			writer.println("\tCaught exceptions:");
			status.errors().forEach(e -> writer.println("\t\t" + e.getMessage()));
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

}
