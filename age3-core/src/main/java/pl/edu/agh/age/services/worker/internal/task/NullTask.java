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

import com.google.common.util.concurrent.ListenableScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

@SuppressWarnings("Singleton")
public final class NullTask implements Task {

	public static final NullTask INSTANCE = new NullTask();

	private static final Logger logger = LoggerFactory.getLogger(NullTask.class);

	private NullTask() {
		// Empty
	}

	@Override public boolean isRunning() {
		logger.warn("Checking 'running' status of the NULL task.");
		return false;
	}

	@Override public String className() {
		throw new UnsupportedOperationException("NULL task does not return values.");
	}

	@Override public AbstractApplicationContext springContext() {
		throw new UnsupportedOperationException("NULL task does not return values.");
	}

	@Override public ListenableScheduledFuture<?> future() {
		throw new UnsupportedOperationException("NULL task does not return values.");
	}

	@Override public Runnable runnable() {
		throw new UnsupportedOperationException("NULL task does not return values.");
	}

	@Override public void pause() {
		logger.warn("Pausing up NULL task.");
	}

	@Override public void resume() {
		logger.warn("Resuming NULL task.");
	}

	@Override public void stop() {
		logger.warn("Stopping NULL task.");
	}

	@Override public void cleanUp() {
		logger.warn("Cleaning up NULL task.");
	}

	@Override public void cancel() {
		logger.warn("Cancelling NULL task.");
	}

	@Override public boolean equals(final Object obj) {
		return obj instanceof NullTask;
	}

	@Override public int hashCode() {
		return NullTask.class.hashCode();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
