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

package pl.edu.agh.age.integration;

import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import pl.edu.agh.age.services.discovery.DiscoveryEvent;
import pl.edu.agh.age.services.discovery.MemberAddedEvent;
import pl.edu.agh.age.services.discovery.internal.HazelcastDiscoveryService;
import pl.edu.agh.age.services.discovery.internal.HazelcastObjectNames;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.identity.NodeType;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring-test-node.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public final class DiscoveryIT {

	@Inject private HazelcastDiscoveryService discoveryService;

	@Inject private HazelcastInstance hazelcastInstance;

	@Inject private NodeIdentityService identityService;

	@Inject private EventBus eventBus;

	private @MonotonicNonNull IMap<String, NodeDescriptor> members;

	private @Mock(serializable = true) NodeDescriptor nodeDescriptor;

	private final List<DiscoveryEvent> events = newCopyOnWriteArrayList();

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);
		members = hazelcastInstance.getMap(HazelcastObjectNames.MEMBERS_MAP);
		eventBus.register(this);
		events.clear();
	}

	@Test public void testIfIsRunning() {
		assertThat(discoveryService.isRunning()).isTrue();
	}

	@Test public void testIfServicePutsItselfIntoMap() throws InterruptedException {
		final String nodeId = identityService.nodeId();
		// Give it some time to put entry into map
		TimeUnit.SECONDS.sleep(10L);

		assertThat(members).containsOnlyKeys(nodeId);
	}

	@Test public void testIfServiceCreatesEvents() throws InterruptedException {
		final String nodeId = "test-id";
		when(nodeDescriptor.type()).thenReturn(NodeType.COMPUTE);
		when(nodeDescriptor.id()).thenReturn(nodeId);

		members.put(nodeId, nodeDescriptor);

		TimeUnit.SECONDS.sleep(1L);

		final MemberAddedEvent memberAddedEvent = new MemberAddedEvent(nodeId, NodeType.COMPUTE);
		assertThat(events).usingElementComparatorIgnoringFields("timestamp").contains(memberAddedEvent);
	}

	@Subscribe public void listenForEvents(final DiscoveryEvent event) {
		events.add(event);
	}
}
