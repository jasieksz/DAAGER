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
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.api.WorkerAddress;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Set;

final class UnicastMessengerMessage implements Serializable {

	private static final long serialVersionUID = 8710738856544239311L;

	private final WorkerAddress sender;

	private final Set<WorkerAddress> recipients;

	private final Serializable payload;

	UnicastMessengerMessage(final WorkerAddress sender, final Set<WorkerAddress> recipients,
	                        final Serializable payload) {
		requireNonNull(recipients);
		checkState(!recipients.isEmpty(), "Recipients set cannot be empty.");

		this.sender = requireNonNull(sender);
		this.recipients = ImmutableSet.copyOf(recipients);
		this.payload = requireNonNull(payload);
	}

	public WorkerAddress sender() {
		return sender;
	}

	public Set<WorkerAddress> recipients() {
		return recipients;
	}

	public boolean isRecipient(final WorkerAddress workerAddress) {
		assert nonNull(workerAddress);
		return recipients.contains(workerAddress);
	}

	public Serializable payload() {
		return payload;
	}

	@Override public String toString() {
		return toStringHelper(this).add("recipients", recipients).add("sender", sender).addValue(payload).toString();
	}
}
