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

package pl.edu.agh.age.compute.ea;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

import io.vavr.collection.Seq;

@ThreadSafe
public final class StepCountStopCondition implements StopCondition {

	private static final Logger logger = LoggerFactory.getLogger(StepCountStopCondition.class);

	private final int desiredStepCount;

	public StepCountStopCondition(final int desiredStepCount) {
		checkArgument(desiredStepCount > 0, "Step count cannot be less than 0.");
		this.desiredStepCount = desiredStepCount;
	}

	@Override public boolean isReached(final Manager manager) {
		final Seq<Long> stepNmbers = manager.statisticsForKey(StatisticsKeys.STEP_NUMBER, Long.class);
		return stepNmbers.find(l -> l >= desiredStepCount).isDefined();
	}

	@Override public String toString() {
		return toStringHelper(this).add("desiredStepCount", desiredStepCount).toString();
	}
}
