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

package pl.edu.agh.age.services.identity.internal;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.identity.NodeType;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.HazelcastInstance;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class DefaultNodeIdentityService implements NodeIdentityService {

	private static final Logger log = LoggerFactory.getLogger(DefaultNodeIdentityService.class);

	// XXX: We can force all services to implement one interface.
	// We are defining by a class name in order to not depend on them in compile time.
	private static final Set<String> SERVICES_NAMES = ImmutableSet.of(
			"pl.edu.agh.age.services.discovery.DiscoveryService",
			"pl.edu.agh.age.services.lifecycle.NodeLifecycleService",
			"pl.edu.agh.age.services.topology.TopologyService", "pl.edu.agh.age.services.worker.WorkerService",
			"pl.edu.agh.age.services.identity.NodeIdentityService");

	private final UUID nodeId;

	private final String encodedNodeId;

	private NodeType nodeType = NodeType.UNKNOWN;

	private final ApplicationContext applicationContext;

	@Inject public DefaultNodeIdentityService(final HazelcastInstance hazelcastInstance,
	                                          final ApplicationContext applicationContext) {
		encodedNodeId = hazelcastInstance.getLocalEndpoint().getUuid();
		nodeId = UUID.fromString(encodedNodeId);
		this.applicationContext = applicationContext;
	}

	@PostConstruct private void construct() {
		log.debug("Constructing identity service.");
		nodeType = NodeType.COMPUTE;
		log.info("Node type: {}.", nodeType);
		log.info("Node id: {}.", encodedNodeId);
	}

	@Override public String nodeId() {
		return encodedNodeId;
	}

	@Override public NodeDescriptor descriptor() {
		return new NodeDescriptor(encodedNodeId, nodeType, services());
	}

	@Override public Set<String> services() {
		// FIXME: Does not work with multiple implementations
		return SERVICES_NAMES.parallelStream().filter(service -> {
			try {
				final Class<?> aClass = Class.forName(service);
				applicationContext.getBean(aClass);
				return true;
			} catch (final ClassNotFoundException | NoSuchBeanDefinitionException e) {
				log.debug("No service {} - {}.", service, e.getMessage());
				return false;
			}
		}).collect(Collectors.<@Initialized @NonNull String> toSet());
	}

	@Override public boolean isCompute() {
		return is(NodeType.COMPUTE);
	}

	@Override public boolean is(final NodeType type) {
		return nodeType == requireNonNull(type);
	}
}
