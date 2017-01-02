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

package pl.edu.agh.age.util;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public final class HazelcastFactory {
	private static final Logger logger = LoggerFactory.getLogger(HazelcastFactory.class);

	private HazelcastFactory() {}

	@Bean
	public static HazelcastInstance create(@Value("${age.node.hazelcast.config.main}")
	                                       final String hazelcastMainConfig) {
		System.setProperty("hazelcast.logging.type", "slf4j");
		// Update to a default value if not provided
		final String hazelcastConfig = NodeSystemProperties.HAZELCAST_CONFIG_USER.get();
		NodeSystemProperties.HAZELCAST_CONFIG_USER.set(hazelcastConfig);
		logger.debug("Hazelcast user configuration is loaded from: {}", hazelcastConfig);

		final Config config = new ClasspathXmlConfig(hazelcastMainConfig);
		return Hazelcast.newHazelcastInstance(config);
	}

}
