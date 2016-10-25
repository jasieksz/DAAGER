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

import pl.edu.agh.age.client.StatusServiceClient;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.status.Status;
import pl.edu.agh.age.services.status.internal.DefaultStatusService;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastStatusServiceClient implements StatusServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(HazelcastStatusServiceClient.class);

	private IMap<String, Status> statusMap;

	@Inject public HazelcastStatusServiceClient(final HazelcastInstance hazelcastInstance) {
		statusMap = hazelcastInstance.getMap(DefaultStatusService.MAP_NAME);
	}

	@Override public @Nullable Status getStatusForNode(final NodeDescriptor descriptor) {
		return statusMap.get(descriptor.id());
	}
}
