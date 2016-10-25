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
import static com.google.common.collect.Sets.newConcurrentHashSet;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.api.BroadcastMessenger;
import pl.edu.agh.age.compute.api.MessageListener;
import pl.edu.agh.age.services.topology.TopologyService;
import pl.edu.agh.age.services.worker.WorkerMessage;

import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ThreadSafe
public final class DefaultBroadcastMessenger implements BroadcastMessenger, CommunicationFacility {

	private static final Logger log = LoggerFactory.getLogger(DefaultBroadcastMessenger.class);

	private final Set<MessageListener<Serializable>> listeners = newConcurrentHashSet();

	private final TopologyService topologyService;

	private final WorkerCommunication workerCommunication;

	@Inject
	public DefaultBroadcastMessenger(final WorkerCommunication workerCommunication,
	                                 final TopologyService topologyService) {
		this.workerCommunication = workerCommunication;
		this.topologyService = topologyService;
	}

	@Override public void send(final Serializable message) {
		log.debug("Sending message {}.", message);
		final Set<String> neighbours = topologyService.neighbours();
		final WorkerMessage<Serializable> workerMessage = WorkerMessage.createWithPayload(
				WorkerMessage.Type.BROADCAST_MESSAGE, neighbours, message);
		log.debug("Prepared message to send: {}.", workerMessage);
		workerCommunication.sendMessage(workerMessage);
	}

	@Override public <T extends Serializable> boolean onMessage(final WorkerMessage<T> workerMessage) {
		log.debug("Received worker service message {}.", workerMessage);
		requireNonNull(workerMessage);

		if (workerMessage.hasType(WorkerMessage.Type.BROADCAST_MESSAGE)) {
			final Serializable message = workerMessage.requiredPayload();
			listeners.parallelStream().forEach(listener -> listener.onMessage(message));

			return true;
		}

		return false;
	}

	@Override public Set<WorkerMessage.Type> subscribedTypes() {
		return ImmutableSet.of(WorkerMessage.Type.BROADCAST_MESSAGE);
	}

	@Override public void start() {
		log.debug("Starting local broadcast messenger.");
	}

	@Override public <T extends Serializable> void registerListener(final MessageListener<T> listener) {
		log.debug("Adding listener {}.", listener);
		listeners.add((MessageListener<Serializable>)listener);
	}

	@Override public <T extends Serializable> void removeListener(final MessageListener<T> listener) {
		log.debug("Removing listener {}.", listener);
		listeners.remove(listener);
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
