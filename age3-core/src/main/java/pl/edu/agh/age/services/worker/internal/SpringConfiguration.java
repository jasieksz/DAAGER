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

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.worker.internal.task.TaskBuilder;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SpringConfiguration implements WorkerConfiguration {

	private static final long serialVersionUID = 4719974331488707814L;

	private final String configuration;

	private SpringConfiguration(final String configuration) {
		this.configuration = requireNonNull(configuration);
	}

	public static SpringConfiguration fromFilesystem(final String pathToFile) throws IOException {
		requireNonNull(pathToFile);

		final Path path = Paths.get(pathToFile);
		if (!Files.exists(path)) {
			throw new FileNotFoundException("Configuration file " + pathToFile + " does not exist.");
		}

		return new SpringConfiguration(new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
	}

	public static SpringConfiguration fromClasspath(final String pathToFile) throws IOException {
		requireNonNull(pathToFile);

		try (final InputStream in = SpringConfiguration.class.getClassLoader().getResourceAsStream(pathToFile)) {
			return new SpringConfiguration(CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8)));
		}
	}

	@Override public TaskBuilder taskBuilder() {
		return TaskBuilder.fromString(configuration);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(configuration.substring(0, 10)).toString();
	}
}
