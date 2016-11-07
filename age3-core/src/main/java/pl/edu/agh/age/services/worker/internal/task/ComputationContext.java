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

import static com.google.common.base.Preconditions.checkState;

import pl.edu.agh.age.compute.api.DistributionUtilities;
import pl.edu.agh.age.services.worker.internal.CommunicationFacility;
import pl.edu.agh.age.services.worker.internal.DefaultThreadPool;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;

import com.google.common.base.MoreObjects;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ComputationContext {

	private static final Logger logger = LoggerFactory.getLogger(ComputationContext.class);

	private final WorkerConfiguration configuration;

	private final Set<CommunicationFacility> communicationFacilities;

	private final DistributionUtilities computeDistributionUtilities;

	private final DefaultThreadPool computeThreadPool = new DefaultThreadPool();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private @GuardedBy("lock") @MonotonicNonNull AbstractApplicationContext springContext = null;

	private @GuardedBy("lock") @MonotonicNonNull Task currentTask = null;

	private @GuardedBy("lock") boolean cleaned = false;

	public ComputationContext(final WorkerConfiguration configuration,
	                          final Set<CommunicationFacility> communicationFacilities,
	                          final DistributionUtilities computeDistributionUtilities) {
		this.configuration = configuration;
		this.communicationFacilities = communicationFacilities;
		this.computeDistributionUtilities = computeDistributionUtilities;
	}

	public String currentTaskDescription() {
		lock.readLock().lock();
		try {
			return (currentTask == null) ? "(null)" : currentTask.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@EnsuresNonNull("currentTask") private void checkIfTaskIsActive() {
		lock.readLock().lock();
		try {
			checkState(currentTask != null, "Task was not started");
			checkState(!cleaned, "Task was already cleaned");
		} finally {
			lock.readLock().unlock();
		}
	}

	public void startTask(final ListeningScheduledExecutorService executorService,
	                      final FutureCallback<Object> callback) {
		lock.writeLock().lock();
		try {
			checkState(currentTask == null, "Task already started");
			final TaskBuilder taskBuilder = createTaskBuilder();
			springContext = taskBuilder.springContext();
			currentTask = taskBuilder.buildAndSchedule(executorService, callback);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void pause() {
		lock.writeLock().lock();
		try {
			checkIfTaskIsActive();
			logger.debug("Pausing current task {}", currentTask);
			currentTask.pause();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void resume() {
		lock.writeLock().lock();
		try {
			checkIfTaskIsActive();
			logger.debug("Resuming current task {}", currentTask);
			currentTask.resume();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void stop() {
		lock.writeLock().lock();
		try {
			checkIfTaskIsActive();
			logger.debug("Stopping current task {}", currentTask);
			currentTask.stop();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void cleanUp() {
		lock.writeLock().lock();
		try {
			checkIfTaskIsActive();
			logger.debug("Cleaning up after task {}", currentTask);
			currentTask.cleanUp();
			cleaned = true;
			logger.debug("Cleaning up after task");
			springContext.destroy();
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void cancel() {
		lock.writeLock().lock();
		try {
			checkIfTaskIsActive();
			logger.debug("Cancelling current task {}", currentTask);
			currentTask.cancel();
		} finally {
			lock.writeLock().unlock();
		}
	}

	private TaskBuilder createTaskBuilder() {
		final TaskBuilder taskBuilder = configuration.taskBuilder();
		communicationFacilities.forEach(taskBuilder::registerSingleton);
		taskBuilder.registerSingleton(computeThreadPool);
		taskBuilder.registerSingleton(computeDistributionUtilities);

		// Refreshing the Spring context
		taskBuilder.finishConfiguration();

		return taskBuilder;
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this).add("currentTask", currentTask).add("cleaned", cleaned).toString();
	}
}
