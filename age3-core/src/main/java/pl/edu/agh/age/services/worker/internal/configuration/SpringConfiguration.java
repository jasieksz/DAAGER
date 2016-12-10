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

package pl.edu.agh.age.services.worker.internal.configuration;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Maps.newHashMap;

import pl.edu.agh.age.services.worker.FailedComputationSetupException;
import pl.edu.agh.age.services.worker.internal.task.TaskBuilder;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public final class SpringConfiguration implements WorkerConfiguration {

	private static final long serialVersionUID = 4719974331488707814L;

	private final String resourceInfo;

	private final String configuration;

	private final Map<String, Object> properties = newHashMap();

	public SpringConfiguration(final Resource resource, final Map<String, Object> properties) throws IOException {
		resourceInfo = String.format("%s, length=%d", resource.getURL(), resource.contentLength());
		configuration = CharStreams.toString(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8));
		this.properties.putAll(properties);
	}

	@Override public TaskBuilder taskBuilder() throws FailedComputationSetupException {
		return TaskBuilder.fromString(configuration, properties);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(resourceInfo).toString();
	}
}
