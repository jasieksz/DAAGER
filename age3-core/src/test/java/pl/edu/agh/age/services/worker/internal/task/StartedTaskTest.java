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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.AbstractApplicationContext;

public final class StartedTaskTest {

	@Mock private AbstractApplicationContext context;

	@Mock private Runnable runnable;

	@Mock private ListenableScheduledFuture<?> future;

	private StartedTask task;

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);

		task = new StartedTask("", runnable, future);
	}

	@Test public void testIsRunning() {
		when(future.isDone()).thenReturn(false);

		assertThat(task.isRunning()).isTrue();
	}

	@Test public void testIsNotRunning() {
		when(future.isDone()).thenReturn(true);

		assertThat(task.isRunning()).isFalse();
	}

	@Test public void testPause_shouldNotDoAnything() {
		when(future.isDone()).thenReturn(false);

		task.pause();

		assertThat(task.isRunning()).isTrue();
	}

	@Test public void testResume_shouldNotDoAnything() {
		when(future.isDone()).thenReturn(false);

		task.resume();

		assertThat(task.isRunning()).isTrue();
	}

	@Test public void testStop() {
		when(future.isDone()).thenReturn(false);
		when(future.cancel(anyBoolean())).thenReturn(true);

		task.stop();
	}

	@Test public void testStop_shouldNotStopStoppedTask() {
		when(future.isDone()).thenReturn(true);

		task.stop();

		verify(future, never()).cancel(anyBoolean());
	}

	@Test public void testCleanUp() {
		when(future.isDone()).thenReturn(true);

		task.cleanUp();
	}

	@Test(expected = IllegalStateException.class) public void testCleanUp_shouldNotWorkOnRunningTasks() {
		when(future.isDone()).thenReturn(false);

		task.cleanUp();
	}

	@Test public void testCancel() {
		when(future.isDone()).thenReturn(false);
		when(future.cancel(anyBoolean())).thenReturn(true);

		task.stop();
	}
}
