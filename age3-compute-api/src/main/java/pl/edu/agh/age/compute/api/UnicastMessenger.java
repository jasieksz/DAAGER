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
package pl.edu.agh.age.compute.api;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Messenger for the unicast communication between workers.
 *
 * <p>Implementations need to be thread-safe, as they are presented to possibly multi-threaded compute code.
 */
@ThreadSafe
public interface UnicastMessenger {

	WorkerAddress address();

	Set<WorkerAddress> neighbours();

	/**
	 * Sends the message to the specified worker.
	 *
	 * @param receiver
	 * 		a recipient of the message.
	 * @param message
	 * 		a message to send.
	 * @param <T>
	 * 		a type of the payload.
	 */
	<T extends Serializable> void send(WorkerAddress receiver, T message);

	/**
	 * Sends the message to the specified workers.
	 *
	 * @param receivers
	 * 		a set of recipients of the message.
	 * @param message
	 * 		a message to send.
	 * @param <T>
	 * 		a type of the payload.
	 */
	<T extends Serializable> void send(Set<WorkerAddress> receivers, T message);

	/**
	 * Registers a listener that will receive all incoming messages target for this address (obtained via {@link
	 * #address()}).
	 *
	 * @param listener
	 * 		a listener to register.
	 * @param <T>
	 * 		a type of the payload.
	 */
	<T extends Serializable> void registerListener(UnicastMessageListener<T> listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener
	 * 		a listener to remove.
	 * @param <T>
	 * 		a type of the payload.
	 */
	<T extends Serializable> void removeListener(UnicastMessageListener<T> listener);
}
