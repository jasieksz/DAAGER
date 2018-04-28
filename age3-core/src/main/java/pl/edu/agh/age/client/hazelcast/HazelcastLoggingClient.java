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

import pl.edu.agh.age.client.LoggingClient;

import com.google.common.collect.ImmutableList;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import ch.qos.logback.classic.spi.ILoggingEvent;

@Named
public final class HazelcastLoggingClient implements LoggingClient {

	private static final Logger logger = LoggerFactory.getLogger(HazelcastLoggingClient.class);

	private final HazelcastInstance hazelcastInstance;

	@Inject public HazelcastLoggingClient(final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	@Override public Set<String> availableLogs() {
		return hazelcastInstance.getSet("logging/list");
	}

	@Override public List<ILoggingEvent> logFor(final String id) {
		final String path = "logging/node/" + id;
		logger.debug("Getting logs from {}", path);
		return ImmutableList.copyOf(hazelcastInstance.getList(path));
	}

	@Override public int countFor(final String id) {
		final String path = "logging/node/" + id;
		logger.debug("Getting count from {}", path);
		return hazelcastInstance.getList(path).size();
	}
}
