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
package pl.edu.agh.age.services.worker.internal;

import static com.google.common.base.MoreObjects.toStringHelper;

import pl.edu.agh.age.compute.api.WorkerAddress;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Default implementation of a compute-level address.
 */

@ThreadSafe
public final class DefaultWorkerAddress implements WorkerAddress {

	private static final long serialVersionUID = 1526560233585684436L;

	private final UUID uuid = UUID.randomUUID();

	private final String encodedUUID = uuid.toString();

	@Override public boolean equals(final @Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DefaultWorkerAddress)) {
			return false;
		}
		final DefaultWorkerAddress other = (DefaultWorkerAddress)obj;
		return Objects.equals(uuid, other.uuid);
	}

	@Override public int hashCode() {
		return Objects.hashCode(uuid);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(encodedUUID).toString();
	}
}
