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

package pl.edu.agh.age.services.topology.internal;

import pl.edu.agh.age.services.discovery.DiscoveryService;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.topology.TopologyMessage;
import pl.edu.agh.age.services.topology.TopologyService;

import com.google.common.eventbus.EventBus;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

//@Named("non-participating")
public final class NonParticipatingTopologyService implements TopologyService {

	public static final String CONFIG_MAP_NAME = "topology/config";

	public static final String CHANNEL_NAME = "topology/channel";

	private static final Logger log = LoggerFactory.getLogger(NonParticipatingTopologyService.class);

	@Inject @MonotonicNonNull private HazelcastInstance hazelcastInstance;

	@Inject private @MonotonicNonNull DiscoveryService discoveryService;

	@Inject @MonotonicNonNull private EventBus eventBus;

	@MonotonicNonNull private IMap<String, Object> runtimeConfig;

	@MonotonicNonNull private ITopic<TopologyMessage> topic;

	@PostConstruct private void construct() {
		log.debug("Constructing NonParticipatingTopologyService.");
		// Obtain dependencies
		runtimeConfig = hazelcastInstance.getMap(CONFIG_MAP_NAME);
		topic = hazelcastInstance.getTopic(CHANNEL_NAME);

		topic.addMessageListener(new DistributedMessageListener());
		eventBus.register(this);
	}

	@Override public Set<String> neighbours() {
		throw new UnsupportedOperationException("Neighbourhood not available for this implementation.");
	}

	@Override public Optional<String> masterId() {
		return Optional.ofNullable((String)runtimeConfig.get(ConfigKeys.MASTER));
	}

	@Override public boolean isLocalNodeMaster() {
		return false;
	}

	@Override public boolean hasTopology() {
		return false;
	}

	@Override public Optional<DirectedGraph<String, DefaultEdge>> topologyGraph() {
		return Optional.ofNullable((DirectedGraph<String, DefaultEdge>)runtimeConfig.get(ConfigKeys.TOPOLOGY_GRAPH));
	}

	@Override public Optional<String> topologyType() {
		return Optional.ofNullable((String)runtimeConfig.get(ConfigKeys.TOPOLOGY_TYPE));
	}

	protected Set<NodeDescriptor> getComputeNodes() {
		return discoveryService.membersMatching("type = 'compute'");
	}

	private static class ConfigKeys {
		public static final String MASTER = "master";

		public static final String TOPOLOGY_GRAPH = "topologyGraph";

		public static final String TOPOLOGY_TYPE = "topologyType";
	}

	private class DistributedMessageListener implements MessageListener<TopologyMessage> {
		@Override public void onMessage(final Message<TopologyMessage> message) {
			log.debug("Distributed event: {}", message);
			final TopologyMessage topologyMessage = message.getMessageObject();
			switch (topologyMessage.getType()) {
				case TOPOLOGY_SELECTED:

					break;
			}
		}
	}
}
