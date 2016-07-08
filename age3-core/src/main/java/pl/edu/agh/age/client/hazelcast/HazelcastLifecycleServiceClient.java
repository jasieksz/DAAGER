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

import static pl.edu.agh.age.services.lifecycle.internal.DefaultNodeLifecycleService.CHANNEL_NAME;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.services.lifecycle.LifecycleMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastLifecycleServiceClient implements LifecycleServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(HazelcastLifecycleServiceClient.class);

	@Inject private HazelcastInstance hazelcastInstance;

	private ITopic<LifecycleMessage> topic;

	@PostConstruct private void construct() {
		topic = hazelcastInstance.getTopic(CHANNEL_NAME);
	}

	@Override public void destroyCluster() {
		logger.debug("Destroying cluster");
		topic.publish(LifecycleMessage.createWithoutPayload(LifecycleMessage.Type.DESTROY));
	}
}
