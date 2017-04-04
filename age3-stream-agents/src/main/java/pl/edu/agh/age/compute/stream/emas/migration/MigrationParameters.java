/*
 * Copyright (C) 2016-2017 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.stream.emas.migration;

import static com.google.common.base.Preconditions.checkArgument;

public class MigrationParameters {

	private final long stepInterval;

	private final double migrationProbability;

	public MigrationParameters(final long stepInterval, final double migrationProbability) {
		checkArgument(stepInterval >= 0);
		checkArgument(migrationProbability >= 0 && migrationProbability <= 1,
		    "Migration probability has invalid value");
		this.stepInterval = stepInterval;
		this.migrationProbability = migrationProbability;
	}

	public long stepInterval() {
		return stepInterval;
	}

	public double migrationProbability() {
		return migrationProbability;
	}

}
