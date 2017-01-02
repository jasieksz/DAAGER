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

/**
 * System properties used by the node. See `docs/user/properties.md` for details.
 */
public enum NodeSystemProperties {
	CONFIG("age.node.config", "spring-node.xml"),
	HAZELCAST_CONFIG_MAIN("age.node.hazelcast.config.main", "classpath:hazelcast-node.xml"),
	HAZELCAST_CONFIG_USER("age.node.hazelcast.config.user", "classpath:hazelcast-network.xml"),
	HAZELCAST_APPENDER("age.node.hazelcast.appender", "false");

	public final String propertyName;

	public final String defaultValue;

	NodeSystemProperties(final String propertyName, final String defaultValue) {
		this.propertyName = propertyName;
		this.defaultValue = defaultValue;
	}

	@SuppressWarnings("AccessOfSystemProperties") public String get() {
		return System.getProperty(propertyName, defaultValue);
	}

	@SuppressWarnings("AccessOfSystemProperties") public String set(final String value) {
		return System.setProperty(propertyName, value);
	}
}
