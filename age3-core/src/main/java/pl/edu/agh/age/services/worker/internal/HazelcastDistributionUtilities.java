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

package pl.edu.agh.age.services.worker.internal;

import pl.edu.agh.age.compute.api.DistributionUtilities;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

final class HazelcastDistributionUtilities implements DistributionUtilities {

	private static final Logger logger = LoggerFactory.getLogger(HazelcastDistributionUtilities.class);

	private final HazelcastInstance hazelcastInstance;

	private final Set<DistributedObject> distributedObjects = new HashSet<>(10);

	public HazelcastDistributionUtilities(final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	@Override public <K, V> IMap<K, V> getMap(final String name) {
		final IMap<K, V> map = hazelcastInstance.getMap("compute/" + name);
		distributedObjects.add(map);
		return map;
	}

	@Override public IdGenerator getIdGenerator(final String name) {
		final IdGenerator idGenerator = hazelcastInstance.getIdGenerator("compute/" + name);
		distributedObjects.add(idGenerator);
		return idGenerator;
	}

	@Override public void reset() {
		logger.debug("Distributed utilities destroy");
		distributedObjects.forEach(DistributedObject::destroy);
		distributedObjects.clear();
	}
}
