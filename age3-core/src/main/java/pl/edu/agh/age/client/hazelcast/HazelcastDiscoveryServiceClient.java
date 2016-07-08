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

package pl.edu.agh.age.client.hazelcast;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.services.discovery.internal.HazelcastDiscoveryService.MEMBERS_MAP;

import pl.edu.agh.age.client.DiscoveryServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastDiscoveryServiceClient implements DiscoveryServiceClient {
	@Inject private HazelcastInstance hazelcastInstance;

	private IMap<String, NodeDescriptor> members;

	@PostConstruct private void construct() {
		members = hazelcastInstance.getMap(MEMBERS_MAP);
	}

	@Override public Set<NodeDescriptor> membersMatching(final String criteria) {
		return ImmutableSet.copyOf(members.values(new SqlPredicate(requireNonNull(criteria))));
	}

	@Override public Set<NodeDescriptor> allMembers() {
		return ImmutableSet.copyOf(members.values());
	}

	@Override public NodeDescriptor memberWithId(final String id) {
		final NodeDescriptor descriptor = members.get(requireNonNull(id));
		if (isNull(descriptor)) {
			throw new NullPointerException("No such member."); // FIXME: Better exception type
		}
		return descriptor;
	}
}
