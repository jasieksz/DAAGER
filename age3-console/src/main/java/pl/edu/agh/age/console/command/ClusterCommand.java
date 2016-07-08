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
import static java.util.Objects.isNull;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.client.StatusServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeType;
import pl.edu.agh.age.services.status.Status;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Command for getting info about and managing the whole cluster.
 */
@Named
@Scope("prototype")
@Parameters(commandNames = "cluster", commandDescription = "Cluster management", optionPrefixes = "--")
public final class ClusterCommand extends BaseCommand {

	private enum Operation {
		NODES("nodes"),
		DESTROY("destroy");

		private final String operationName;

		Operation(final String operationName) {
			this.operationName = operationName;
		}

		public String operationName() {
			return operationName;
		}
	}

	private static final Logger log = LoggerFactory.getLogger(ClusterCommand.class);

	@Inject private DiscoveryServiceClient discoveryService;

	@Inject private LifecycleServiceClient lifecycleServiceClient;

	@Inject private StatusServiceClient statusServiceClient;

	@Parameter(names = "--long") private boolean longOutput = false;

	@Parameter(names = "--id") private @MonotonicNonNull String id;

	public ClusterCommand() {
		addHandler(Operation.NODES.operationName(), this::nodes);
		addHandler(Operation.DESTROY.operationName(), this::destroy);
	}

	/**
	 * Prints information about nodes (multiple or single).
	 */
	private void nodes(final Terminal printWriter) {
		log.debug("Printing information about nodes.");
		if (isNull(id)) {
			final Set<NodeDescriptor> neighbours = discoveryService.allMembers();
			neighbours.forEach(descriptor -> printNode(descriptor, printWriter));
		} else {
			final NodeDescriptor descriptor = discoveryService.memberWithId(id);
			printNode(descriptor, printWriter);
		}
	}

	/**
	 * Destroys the cluster.
	 */
	private void destroy(final Terminal printWriter) {
		lifecycleServiceClient.destroyCluster();
	}

	private void printNode(final NodeDescriptor descriptor, final Terminal printWriter) {
		printWriter.writer().println(String.format("%s - %s", descriptor.id(), descriptor.type()));
		if (longOutput && (descriptor.type() == NodeType.COMPUTE)) {
			final @Nullable Status status = statusServiceClient.getStatusForNode(descriptor);
			if (isNull(status)) {
				printWriter.writer().println("\tNo status for the node. Seems to be an error.");
				return;
			}
			printWriter.writer()
			           .println("\tLast updated: " + status.creationTimestamp()
			                                               .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			printWriter.writer().println("\tCaught exceptions:");
			status.errors().forEach(e -> printWriter.writer().println("\t\t" + e.getMessage()));
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
