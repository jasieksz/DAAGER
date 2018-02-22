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

package pl.edu.agh.age.services.discovery.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.identity.NodeType;

import com.google.common.eventbus.EventBus;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

public final class HazelcastDiscoveryServiceTest {

	@Mock private EventBus eventBus;

	@Mock private NodeIdentityService identityService;

	private IMap<String, NodeDescriptor> members;

	private String uuid;

	private HazelcastInstance hazelcastInstance;

	private HazelcastDiscoveryService serviceUnderTest;

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);

		hazelcastInstance = Hazelcast.newHazelcastInstance();
		uuid = hazelcastInstance.getLocalEndpoint().getUuid();
		when(identityService.nodeId()).thenReturn(uuid);
		when(identityService.descriptor()).thenReturn(
			new pl.edu.agh.age.services.identity.internal.NodeDescriptor(uuid, NodeType.COMPUTE,
			                                                             Collections.emptySet()));

		members = hazelcastInstance.getMap(HazelcastObjectNames.MEMBERS_MAP);
		serviceUnderTest = new HazelcastDiscoveryService(hazelcastInstance, eventBus, identityService);
		serviceUnderTest.start();
	}

	@After public void tearDown() {
		serviceUnderTest.stop();
		hazelcastInstance.shutdown();
	}

	@Test public void shouldRegisterItselfInTheMap() {
		assertThat(members).as("contains node entry").containsKey(uuid);
		final NodeDescriptor descriptor = members.get(uuid);
		assertThat(descriptor.id()).as("descriptor has correct id").isEqualTo(uuid);
	}

	@Test public void shouldReturnCorrectData() {
		assertThat(serviceUnderTest.allMembers()).hasSize(1);
		assertThat(serviceUnderTest.memberWithId(uuid)).isNotEmpty();
		assertThat(serviceUnderTest.memberWithId("random")).isNotNull().isEmpty();
	}
}
