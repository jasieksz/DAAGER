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
package pl.edu.agh.age.examples;

import static com.google.common.base.MoreObjects.toStringHelper;

import pl.edu.agh.age.compute.api.UnicastMessageListener;
import pl.edu.agh.age.compute.api.UnicastMessenger;
import pl.edu.agh.age.compute.api.WorkerAddress;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * This example shows how the compute level can communicate between nodes using unicast messages.
 *
 * Unicast messaging is performed in regards of the topology defined in the platform level and the messages cane be sent
 * only to neighbours of the current node.
 *
 * Sent messages can have any serializable payload.
 *
 * To receive messages, the runnable must register a {@link UnicastMessageListener} using
 * {@link UnicastMessenger#registerListener}.
 *
 * @see pl.edu.agh.age.services.topology.TopologyService
 */
public final class SimpleWithUnicastCommunication implements Runnable, UnicastMessageListener<@NonNull String> {

	private static final Logger logger = LoggerFactory.getLogger(SimpleWithUnicastCommunication.class);

	private final UnicastMessenger messenger;

	@Inject public SimpleWithUnicastCommunication(final UnicastMessenger messenger) {
		this.messenger = messenger;
	}

	@Override public void run() {
		logger.info("This is the simplest possible example of a computation");
		logger.info("Unicast messenger: {}", messenger);
		logger.info("My address: {}", messenger.address());

		messenger.registerListener(this);

		for (int i = 0; i < 100; i++) {
			logger.info("Iteration {}. Sending message", i);

			// Find any neighbour
			final Optional<WorkerAddress> target = messenger.neighbours().stream().findAny();

			if (target.isPresent()) {
				logger.info("To: {}", target.get());
				messenger.send(target.get(), "Test message from " + messenger.address());
			} else {
				logger.info("I am alone :(");
			}

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (final InterruptedException e) {
				logger.debug("Interrupted", e);
				Thread.currentThread().interrupt();
				return;
			}
		}

		messenger.removeListener(this);
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	@Override public void onUnicastMessage(final @NonNull String message, final @NonNull WorkerAddress sender) {
		logger.info("Message received: {} from {}", message, sender);
	}
}
