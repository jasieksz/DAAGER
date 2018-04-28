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

package pl.edu.agh.age.console;

import com.google.common.base.Splitter;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public final class HazelcastClientFactory {
	private static final Logger logger = LoggerFactory.getLogger(HazelcastClientFactory.class);

	private static final Splitter ADDRESS_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

	private HazelcastClientFactory() {}

	@Bean
	public static HazelcastInstance create() throws IOException {
		System.setProperty("hazelcast.logging.type", "slf4j");
		final String nodes = ConsoleSystemProperties.NODES.get();
		final List<String> addressList = ADDRESS_SPLITTER.splitToList(nodes);
		logger.debug("Hazelcast Client will connect to: {}", addressList);

		try (InputStream in = HazelcastClientFactory.class.getClassLoader()
		                                                  .getResourceAsStream("hazelcast-console.xml")) {
			final ClientConfig config = new XmlClientConfigBuilder(in).build();
			config.getNetworkConfig().setAddresses(addressList);
			return HazelcastClient.newHazelcastClient(config);
		}
	}
}
