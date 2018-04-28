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

package pl.edu.agh.age.compute.stream.emas.migration;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nullable;

public class MigrationParameters {

	private final long stepInterval;

	private final double partToMigrate;

	private final MigrationStrategy migrationStrategy;

	public MigrationParameters(final long stepInterval, final double partToMigrate,
	                           final @Nullable MigrationStrategy migrationStrategy) {
		checkArgument(stepInterval >= 0);
		checkArgument(partToMigrate >= 0 && partToMigrate <= 1, "Population part to migrate has an invalid value");
		this.stepInterval = stepInterval;
		this.partToMigrate = partToMigrate;
		this.migrationStrategy = migrationStrategy != null ? migrationStrategy : MigrationStrategy.RANDOM;
	}

	public long stepInterval() {
		return stepInterval;
	}

	public double partToMigrate() {
		return partToMigrate;
	}

	public MigrationStrategy migrationStrategy() {
		return migrationStrategy;
	}

}
