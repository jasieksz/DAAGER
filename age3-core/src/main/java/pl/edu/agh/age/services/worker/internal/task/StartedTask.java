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
import static java.util.Objects.nonNull;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Wrapper of a single compute task.
 *
 * It is responsible for data consistency of the task.
 */
@ThreadSafe
class StartedTask implements Task {

	private static final Logger log = LoggerFactory.getLogger(StartedTask.class);

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final String className;

	private final AbstractApplicationContext springContext;

	protected final @GuardedBy("lock") Runnable runnable;

	@GuardedBy("lock") private final ListenableScheduledFuture<?> future;

	StartedTask(final String className, final AbstractApplicationContext springContext, final Runnable runnable,
	            final ListenableScheduledFuture<?> future) {
		assert nonNull(className) && nonNull(springContext) && nonNull(runnable) && nonNull(future);

		this.className = className;
		this.springContext = springContext;
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

	@Override public final AbstractApplicationContext springContext() {
		return springContext;
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
		log.debug("The task is not pauseable.");
	}

	@Override public void resume() {
		log.debug("The task is not pauseable.");
	}

	@Override public void stop() {
		if (!isRunning()) {
			log.warn("Task is already stopped.");
			return;
		}

		log.debug("Stopping task {}.", runnable);
		lock.writeLock().lock();
		try {
			final boolean canceled = future.cancel(true);
			if (!canceled) {
				log.warn("Could not cancel the task. Maybe it already stopped?");
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override public void cleanUp() {
		checkState(!isRunning(), "Task is not stopped.");

		log.debug("Cleaning up after task.");
		springContext.destroy();
	}

	@Override public void cancel() {
		if (!isRunning()) {
			log.warn("Task is already stopped.");
			return;
		}

		log.debug("Stopping task {}.", runnable);
		lock.writeLock().lock();
		try {
			final boolean canceled = future.cancel(true);
			if (!canceled) {
				log.warn("Could not cancel the task. Maybe it already stopped?");
			}
		} finally {
			lock.writeLock().unlock();
		}
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
