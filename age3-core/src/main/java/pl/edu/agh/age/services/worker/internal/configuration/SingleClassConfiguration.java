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

package pl.edu.agh.age.services.worker.internal.configuration;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.worker.FailedComputationSetupException;
import pl.edu.agh.age.services.worker.internal.task.TaskBuilder;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SingleClassConfiguration implements WorkerConfiguration {

	private static final long serialVersionUID = 1113065883705198832L;

	private final String className;

	private final List<String> jars;

	public SingleClassConfiguration(final String className) {
		this(className, ImmutableList.of());
	}

	public SingleClassConfiguration(final String className, final List<String> jars) {
		this.className = requireNonNull(className);
		this.jars = ImmutableList.copyOf(jars);
	}

	@Override public TaskBuilder taskBuilder() throws FailedComputationSetupException {
		return TaskBuilder.fromClass(className, jars);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(className).toString();
	}
}
