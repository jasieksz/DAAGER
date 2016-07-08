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
package pl.edu.agh.age.services.topology;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Optional;

public class TopologyMessage implements Serializable {

	public enum Type {
		MASTER_ELECTED(false),
		TOPOLOGY_SELECTED(false);

		private final boolean payloadRequired;

		Type(final boolean payloadRequired) {
			this.payloadRequired = payloadRequired;
		}

		public boolean isPayloadRequired() {
			return payloadRequired;
		}
	}

	private static final long serialVersionUID = -5867847961864763792L;

	private final Type type;

	private final Serializable payload;

	TopologyMessage(final Type type, final @Nullable Serializable payload) {
		this.type = requireNonNull(type);
		this.payload = payload;
	}

	public static TopologyMessage createWithoutPayload(final Type type) {
		checkArgument(!type.isPayloadRequired(), "Message type require payload.");
		return new TopologyMessage(type, null);
	}

	public Type getType() {
		return type;
	}

	public boolean hasType(final Type typeToCheck) {
		return type == requireNonNull(typeToCheck);
	}

	public Optional<@Nullable Serializable> getPayload() {
		return Optional.ofNullable(payload);
	}

	@Override public String toString() {
		return toStringHelper(this).add("type", type).addValue(payload).toString();
	}
}
