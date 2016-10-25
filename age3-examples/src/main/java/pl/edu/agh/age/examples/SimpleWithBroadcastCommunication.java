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

import pl.edu.agh.age.compute.api.BroadcastMessenger;
import pl.edu.agh.age.compute.api.MessageListener;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * The simplest possible computation. Completely detached and having no dependencies and no friends.
 */
public class SimpleWithBroadcastCommunication implements Runnable, MessageListener<@NonNull String> {

	private static final Logger log = LoggerFactory.getLogger(SimpleWithBroadcastCommunication.class);

	private final BroadcastMessenger messenger;

	@Inject public SimpleWithBroadcastCommunication(final BroadcastMessenger messenger) {
		this.messenger = messenger;
	}

	@Override public void run() {
		log.info("This is the simplest possible example of a computation.");
		log.info("Broadcast messenger: {}.", messenger);

		messenger.registerListener(this);

		for (int i = 0; i < 100; i++) {
			log.info("Iteration {}. Sending message.", i);

			messenger.send("Test message from " + hashCode());

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (final InterruptedException e) {
				log.debug("Interrupted.", e);
				Thread.currentThread().interrupt();
				return;
			}
		}

		messenger.removeListener(this);
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	@Override public void onMessage(final @NonNull String message) {
		log.info("Message received: {}.", message);
	}
}
