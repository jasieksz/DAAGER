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

package pl.edu.agh.age.services.worker.internal;

import pl.edu.agh.age.compute.api.DistributionUtilities;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

final class HazelcastDistributionUtilities implements DistributionUtilities {

	private final HazelcastInstance hazelcastInstance;

	public HazelcastDistributionUtilities(final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	public <K, V> IMap<K, V> getMap(final String name) {
		return hazelcastInstance.getMap("compute/" + name);
	}

	@Override public IdGenerator getIdGenerator(final String name) {
		return hazelcastInstance.getIdGenerator("compute/" + name);
	}
}
