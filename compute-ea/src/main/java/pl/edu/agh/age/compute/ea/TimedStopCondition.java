/*
 * Copyright (C) 2016-2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.ea;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.units.qual.Prefix;
import org.checkerframework.checker.units.qual.s;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class TimedStopCondition implements StopCondition {

	private static final Logger logger = LoggerFactory.getLogger(TimedStopCondition.class);

	private final Duration desiredDuration;

	private final @s(Prefix.milli) long desiredDurationAsMillis;

	private final LocalDateTime startedAt = LocalDateTime.now();

	public TimedStopCondition(final Duration desiredDuration) {
		requireNonNull(desiredDuration);
		checkArgument(desiredDuration.getSeconds() > 0L, "Duration cannot be shorter than 1 second.");
		this.desiredDuration = desiredDuration;
		desiredDurationAsMillis = desiredDuration.toMillis();
	}

	public TimedStopCondition(final long seconds) {
		this(Duration.ofSeconds(seconds));
	}

	@Override public boolean isReached(final Manager unused) {
		if (Duration.between(startedAt, LocalDateTime.now()).toMillis() >= desiredDurationAsMillis) {
			logger.info("Time's up");
			return true;
		}
		return false;
	}

	@Override public String toString() {
		return toStringHelper(this).add("startedAt", startedAt).add("duration", desiredDuration).toString();
	}
}
