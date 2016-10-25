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
package pl.edu.agh.age.examples;

import static com.google.common.base.MoreObjects.toStringHelper;

import pl.edu.agh.age.compute.api.BroadcastMessageListener;
import pl.edu.agh.age.compute.api.BroadcastMessenger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * This example shows how the compute level can communicate between nodes using broadcast messages.
 *
 * Broadcast messaging is performed in regards of the topology defined in the platform level and the messages are sent
 * only to neighbours of the current node.
 *
 * Sent messages can have any serializable payload.
 *
 * To receive messages, the runnable must register a {@link BroadcastMessageListener} using
 * {@link BroadcastMessenger#registerListener}.
 *
 * @see pl.edu.agh.age.services.topology.TopologyService
 */
public final class SimpleWithBroadcastCommunication implements Runnable, BroadcastMessageListener<@NonNull String> {

	private static final Logger logger = LoggerFactory.getLogger(SimpleWithBroadcastCommunication.class);

	private final BroadcastMessenger messenger;

	@Inject public SimpleWithBroadcastCommunication(final BroadcastMessenger messenger) {
		this.messenger = messenger;
	}

	@Override public void run() {
		logger.info("This is the simplest possible example of a computation");
		logger.info("Broadcast messenger: {}", messenger);

		messenger.registerListener(this);

		for (int i = 0; i < 100; i++) {
			logger.info("Iteration {}. Sending message", i);

			messenger.send("Test message from " + hashCode());

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (final InterruptedException e) {
				logger.debug("Interrupted.", e);
				Thread.currentThread().interrupt();
				return;
			}
		}

		messenger.removeListener(this);
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	@Override public void onBroadcastMessage(final @NonNull String message) {
		logger.info("Message received: {}", message);
	}
}
