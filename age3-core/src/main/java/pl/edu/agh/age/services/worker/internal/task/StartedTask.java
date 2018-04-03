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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Wrapper of a single compute task.
 *
 * It is responsible for data consistency of the task.
 */
@ThreadSafe
class StartedTask implements Task {

	private static final Logger logger = LoggerFactory.getLogger(StartedTask.class);

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final String className;

	protected final @GuardedBy("lock") Runnable runnable;

	private final @GuardedBy("lock") ListenableScheduledFuture<?> future;

	StartedTask(final String className, final Runnable runnable, final ListenableScheduledFuture<?> future) {
		assert (className != null) && (runnable != null) && (future != null);

		this.className = className;
		this.runnable = runnable;
		this.future = future;
	}

	@Override public final boolean isRunning() {
		lock.readLock().lock();
		try {
			return !future.isDone();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override public final String className() {
		return className;
	}

	/**
	 * @return a future for the running task.
	 *
	 * @throws IllegalStateException
	 * 		when task is not scheduled.
	 */
	@Override public final ListenableScheduledFuture<?> future() {
		lock.readLock().lock();
		try {
			return future;
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * @return the running task.
	 *
	 * @throws IllegalStateException
	 * 		when task is not scheduled.
	 */
	@Override public final Runnable runnable() {
		lock.readLock().lock();
		try {
			return runnable;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override public void pause() {
		logger.debug("The task is not pauseable.");
	}

	@Override public void resume() {
		logger.debug("The task is not pauseable.");
	}

	@Override public void stop() {
		lock.writeLock().lock();
		try {
			if (!isRunning()) {
				logger.warn("Task is already stopped.");
				return;
			}

			logger.debug("Stopping task {}.", runnable);
			final boolean canceled = future.cancel(true);
			if (!canceled) {
				logger.warn("Could not cancel the task. Maybe it has already stopped?");
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override public void cleanUp() {
		checkState(!isRunning(), "Task is not stopped.");
	}

	@Override public void cancel() {
		stop();
	}

	@Override public String toString() {
		lock.readLock().lock();
		try {
			return toStringHelper(this).add("classname", className).add("runnable", runnable).toString();
		} finally {
			lock.readLock().unlock();
		}
	}

}
