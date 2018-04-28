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

package pl.edu.agh.age.util;

import pl.edu.agh.age.services.identity.NodeIdentityService;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.ISet;

import javax.inject.Inject;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.core.AppenderBase;

/**
 * Logback appender providing support for storing logging events in Hazelcast-backed storage.
 *
 * It saves the ID (as String) of the node to the Set: **logging/list**
 * Each logging event is appended to the list: **logging/node/ID**, where ID is replaced by the actual node ID.
 * The stored objects are {@link LoggingEventVO} instances.
 *
 * Configuration is done as described in <https://github.com/qos-ch/logback-extensions/wiki/Spring>.
 * In the logback config `ch.qos.logback.ext.spring.DelegatingLogbackAppender` is used, then this appender is
 * loaded in the Spring config.
 */
public final class HazelcastAppender extends AppenderBase<ILoggingEvent> {

	private final IList<ILoggingEvent> list;

	@Inject
	public HazelcastAppender(final HazelcastInstance hazelcastInstance, final NodeIdentityService identityService) {
		final String id = identityService.nodeId();
		assert !id.isEmpty();

		final ISet<String> registrationList = hazelcastInstance.getSet("logging/list");
		registrationList.add(id);
		list = hazelcastInstance.getList("logging/node/" + id);
	}

	@Override protected void append(final ILoggingEvent eventObject) {
		list.add(LoggingEventVO.build(eventObject));
	}
}
