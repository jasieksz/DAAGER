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

package pl.edu.agh.age.client.hazelcast;

import pl.edu.agh.age.client.TopologyServiceClient;
import pl.edu.agh.age.services.topology.internal.HazelcastObjectNames;
import pl.edu.agh.age.services.topology.internal.TopologyMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastTopologyServiceClient implements TopologyServiceClient {

	private static final Logger log = LoggerFactory.getLogger(HazelcastTopologyServiceClient.class);

	private final IMap<String, Object> runtimeConfig;

	private final ITopic<TopologyMessage> topic;

	@Inject public HazelcastTopologyServiceClient(final HazelcastInstance hazelcastInstance) {
		log.debug("Constructing NonParticipatingTopologyService.");
		// Obtain dependencies
		runtimeConfig = hazelcastInstance.getMap(HazelcastObjectNames.CONFIG_MAP_NAME);
		topic = hazelcastInstance.getTopic(HazelcastObjectNames.CHANNEL_NAME);
	}

	@Override public Optional<String> masterId() {
		return Optional.ofNullable((String)runtimeConfig.get(HazelcastObjectNames.ConfigKeys.MASTER));
	}

	@Override public Optional<Graph<String, DefaultEdge>> topologyGraph() {
		return Optional.ofNullable(
			(Graph<String, DefaultEdge>)runtimeConfig.get(HazelcastObjectNames.ConfigKeys.TOPOLOGY_GRAPH));
	}

	@Override public Optional<String> topologyType() {
		return Optional.ofNullable((String)runtimeConfig.get(HazelcastObjectNames.ConfigKeys.TOPOLOGY_TYPE));
	}

}
