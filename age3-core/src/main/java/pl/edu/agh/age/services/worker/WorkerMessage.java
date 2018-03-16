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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Message exchanged between {@link WorkerService}s.
 *
 * @param <T>
 * 		the payload type.
 */

public final class WorkerMessage<T extends Serializable> implements Serializable {

	public enum Type {
		LOAD_CONFIGURATION(false),
		START_COMPUTATION(false),
		CANCEL_COMPUTATION(false),
		CLEAN_CONFIGURATION(false),

		// Internal
		BROADCAST_MESSAGE(true, false),
		UNICAST_CONTROL(true, false),
		UNICAST_MESSAGE(true, false);

		private final boolean payloadRequired;

		private final boolean broadcast;

		Type() {
			this(true, true);
		}

		Type(final boolean payloadRequired) {
			this(payloadRequired, true);
		}

		Type(final boolean payloadRequired, final boolean broadcast) {
			this.payloadRequired = payloadRequired;
			this.broadcast = broadcast;
		}

		public boolean isPayloadRequired() {
			return payloadRequired;
		}

		public boolean isBroadcast() {
			return broadcast;
		}
	}

	private static final long serialVersionUID = -6353101926420379298L;

	private final Type type;

	private final boolean broadcast;

	private final ImmutableSet<String> recipients;

	private final @Nullable T payload;

	WorkerMessage(final Type type, final @Nullable T payload) {
		this.type = requireNonNull(type);
		checkArgument(type.isBroadcast(), "Message type must allow broadcasts.");
		this.payload = payload;
		recipients = ImmutableSet.of();
		broadcast = true;
	}

	WorkerMessage(final Type type, final Set<String> recipients, final @Nullable T payload) {
		this.type = requireNonNull(type);
		this.recipients = ImmutableSet.copyOf(requireNonNull(recipients));
		checkArgument(!recipients.isEmpty(), "Recipients cannot be empty.");
		broadcast = false;
		this.payload = payload;
	}

	public static WorkerMessage<Serializable> createBroadcastWithoutPayload(final Type type) {
		checkArgument(!type.isPayloadRequired(), "Message type require payload.");
		checkArgument(type.isBroadcast(), "Message type cannot be broadcast.");
		return new WorkerMessage<>(type, null);
	}

	public static WorkerMessage<Serializable> createWithoutPayload(final Type type, final Set<String> recipients) {
		checkArgument(!type.isPayloadRequired(), "Message type require payload.");
		return new WorkerMessage<>(type, recipients, null);
	}

	public static <T extends Serializable> WorkerMessage<T> createBroadcastWithPayload(final Type type,
	                                                                                   final T payload) {
		checkArgument(type.isBroadcast(), "Message type cannot be broadcast.");
		return new WorkerMessage<>(type, requireNonNull(payload));
	}

	public static <T extends Serializable> WorkerMessage<T> createWithPayload(final Type type,
	                                                                          final Set<String> recipients,
	                                                                          final T payload) {
		return new WorkerMessage<>(type, recipients, requireNonNull(payload));
	}

	public Type type() {
		return type;
	}

	public boolean hasType(final Type typeToCheck) {
		return type == requireNonNull(typeToCheck);
	}

	public <X extends T> Optional<X> payload() {
		return Optional.ofNullable((X)payload);
	}

	public Serializable requiredPayload() {
		checkState(payload != null, "No payload to provide");
		return payload;
	}

	public Set<String> recipients() {
		return recipients;
	}

	public boolean isRecipient(final String id) {
		return broadcast || recipients.contains(requireNonNull(id));
	}

	public boolean isBroadcast() {
		return broadcast;
	}

	@Override public String toString() {
		return toStringHelper(this).add("type", type)
		                           .add("broadcast", broadcast)
		                           .add("recipients", recipients)
		                           .addValue(payload)
		                           .toString();
	}
}
