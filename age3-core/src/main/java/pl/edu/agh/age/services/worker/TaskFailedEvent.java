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

package pl.edu.agh.age.services.worker;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.ServiceFailureEvent;

import java.time.LocalDateTime;

/**
 * Event sent when the task running in {@link WorkerService} has failed due to an exception.
 */

public final class TaskFailedEvent implements TaskEvent, ServiceFailureEvent {

	private final Throwable cause;

	private final LocalDateTime timestamp = LocalDateTime.now();

	public TaskFailedEvent(final Throwable cause) {
		this.cause = requireNonNull(cause);
	}

	@Override public String serviceName() {
		return WorkerService.class.getSimpleName();
	}

	@Override public Throwable cause() {
		return cause;
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(cause).toString();
	}
}
