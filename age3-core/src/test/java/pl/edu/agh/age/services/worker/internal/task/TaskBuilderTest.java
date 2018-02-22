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

package pl.edu.agh.age.services.worker.internal.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.when;

import pl.edu.agh.age.runnables.SimpleTestWithProperty;
import pl.edu.agh.age.services.worker.FailedComputationSetupException;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class TaskBuilderTest {

	@Mock private ListeningScheduledExecutorService executorService;

	@Mock private FutureCallback<Object> callback;

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test public void testFromClass() throws FailedComputationSetupException {
		final TaskBuilder taskBuilder = TaskBuilder.fromClass(FromClassUtil.class.getCanonicalName());

		assertThat(taskBuilder.springContext()).isNotNull();
		assertThat(taskBuilder.isConfigured()).isFalse();
	}

	@Test public void testFromClass_notExisting() throws FailedComputationSetupException {
		final TaskBuilder taskBuilder = TaskBuilder.fromClass("org.class.NotExisting");
		assertThatThrownBy(taskBuilder::finishConfiguration).isInstanceOf(FailedComputationSetupException.class);
	}

	@Test public void testBuildAndSchedule() throws FailedComputationSetupException {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);
		final TaskBuilder taskBuilder = TaskBuilder.fromClass(FromClassUtil.class.getCanonicalName());
		taskBuilder.finishConfiguration();

		assertThat(taskBuilder.buildAndSchedule(executorService, callback)).isInstanceOf(StartedTask.class);
	}

	@Test public void testBuildAndSchedule_needsToBeConfigured() throws FailedComputationSetupException {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);
		final TaskBuilder taskBuilder = TaskBuilder.fromClass(FromClassUtil.class.getCanonicalName());

		assertThatThrownBy(() -> taskBuilder.buildAndSchedule(executorService, callback)).isInstanceOf(
			IllegalStateException.class);
	}

	@Test public void testCannotConfigureTwoTimes() throws FailedComputationSetupException {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);
		final TaskBuilder taskBuilder = TaskBuilder.fromClass(FromClassUtil.class.getCanonicalName());

		taskBuilder.finishConfiguration();
		assertThatThrownBy(taskBuilder::finishConfiguration).isInstanceOf(IllegalStateException.class);
	}

	@Test public void testCannotUpdateAfterFinishingConfiguration() throws FailedComputationSetupException {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);
		final TaskBuilder taskBuilder = TaskBuilder.fromClass(FromClassUtil.class.getCanonicalName());
		taskBuilder.finishConfiguration();

		assertThatThrownBy(() -> taskBuilder.registerSingleton(new Object())).isInstanceOf(IllegalStateException.class);
	}

	@Test public void testCreateFromStringAndProperties() throws Exception {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);

		try (InputStream resourceAsStream = getClass().getResourceAsStream("/compute/spring-test-with-property.xml")) {
			final String s = CharStreams.toString(new InputStreamReader(resourceAsStream, Charsets.UTF_8));
			final Properties properties = new Properties();
			properties.setProperty("age.property", "Test property");
			final TaskBuilder taskBuilder = TaskBuilder.fromString(s, properties);
			taskBuilder.finishConfiguration();
			final Task task = taskBuilder.buildAndSchedule(executorService, callback);
			final SimpleTestWithProperty runnable = (SimpleTestWithProperty)task.runnable();

			assertThat(runnable.property).as("Check if runnable has received correct property")
			                             .isEqualTo("Test property");
		}
	}

	@Test public void testThrowExceptionWhenLackingProperties() throws Exception {
		when(executorService.schedule(any(Runnable.class), eq(0L), any(TimeUnit.class))).then(RETURNS_MOCKS);

		try (InputStream resourceAsStream = getClass().getResourceAsStream("/compute/spring-test-with-property.xml")) {
			final String s = CharStreams.toString(new InputStreamReader(resourceAsStream, Charsets.UTF_8));
			final TaskBuilder taskBuilder = TaskBuilder.fromString(s, new Properties());

			assertThatThrownBy(taskBuilder::finishConfiguration).isInstanceOf(FailedComputationSetupException.class);
		}
	}
}
