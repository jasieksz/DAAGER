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
package pl.edu.agh.age.services.lifecycle.internal;

import static com.google.common.collect.Maps.newEnumMap;

import pl.edu.agh.age.services.discovery.DiscoveryServiceStoppingEvent;
import pl.edu.agh.age.services.lifecycle.LifecycleMessage;
import pl.edu.agh.age.services.lifecycle.NodeDestroyedEvent;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;
import pl.edu.agh.age.util.fsm.FSM;
import pl.edu.agh.age.util.fsm.StateMachineService;
import pl.edu.agh.age.util.fsm.StateMachineServiceBuilder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class DefaultNodeLifecycleService implements SmartLifecycle, NodeLifecycleService {

	/**
	 * States of this lifecycle manager (in other words - states of the node).
	 *
	 * @author AGH AgE Team
	 */
	public enum State {
		/**
		 * Initial state of the node.
		 */
		OFFLINE,
		/**
		 * Node has been initialized.
		 */
		RUNNING,
		DISCONNECTED,
		/**
		 * Node has terminated (terminal state).
		 */
		TERMINATED
	}

	/**
	 * Events that can occur in the node.
	 *
	 * @author AGH AgE Team
	 */
	public enum Event {
		/**
		 * Sent by the bootstrapper.
		 */
		START,
		CONNECTION_DOWN,
		RECONNECTED,
		/**
		 * Terminates the node.
		 */
		STOP
	}



	private static final Logger logger = LoggerFactory.getLogger(DefaultNodeLifecycleService.class);

	private final EventBus eventBus;

	private final ITopic<LifecycleMessage> topic;

	private final StateMachineService<State, Event> service;

	private final EnumMap<LifecycleMessage.Type, Consumer<Serializable>> messageHandlers = newEnumMap(
		LifecycleMessage.Type.class);

	@Inject public DefaultNodeLifecycleService(final HazelcastInstance hazelcastInstance, final EventBus eventBus) {
		messageHandlers.put(LifecycleMessage.Type.DESTROY, this::handleDestroy);
		topic = hazelcastInstance.getTopic(HazelcastObjectNames.CHANNEL_NAME);
		this.eventBus = eventBus;

		//@formatter:off
		service = StateMachineServiceBuilder
			.withStatesAndEvents(State.class, Event.class)
			.withName("lifecycle")
			.startWith(State.OFFLINE)
			.terminateIn(State.TERMINATED)

			.in(State.OFFLINE)
				.on(Event.START).execute(this::internalStart).goTo(State.RUNNING)
				.commit()

			.in(State.RUNNING)
				.on(Event.CONNECTION_DOWN).execute(this::connectionDown).goTo(State.DISCONNECTED)
				.commit()

			.in(State.DISCONNECTED)
				.on(Event.RECONNECTED).execute(this::reconnected).goTo(State.RUNNING)
				.commit()

			.inAnyState()
				.on(Event.STOP).execute(this::internalStop).goTo(State.TERMINATED)
				.commit()

			.whenFailedCall(new ExceptionHandler())
			.notifyOn(eventBus)
			.build();
		//@formatter:on
	}

	@Override public boolean isAutoStartup() {
		return true;
	}

	@Override public void stop(final Runnable callback) {
		stop();
		callback.run();
	}

	@Override public void start() {
		logger.debug("Node lifecycle service starting");
		service.fire(Event.START);
	}

	@Override public void stop() {
		logger.debug("Node lifecycle service stopping");
		service.fire(Event.STOP);
		// The context must wait till the termination process will have finished
		try {
			awaitTermination();
		} catch (final InterruptedException ignored) {
			Thread.interrupted();
		}
		logger.info("Node lifecycle service stopped");
	}

	@Override public boolean isRunning() {
		return !(service.isInState(State.OFFLINE) || service.isTerminated());
	}

	@Override public int getPhase() {
		return Integer.MIN_VALUE;
	}

	@Override public void awaitTermination() throws InterruptedException {
		logger.debug("Awaiting termination");
		service.awaitTermination();
	}

	@Override public boolean isTerminated() {
		return service.isTerminated();
	}

	// Transitions

	private void internalStart(final FSM<State, Event> fsm) {
		logger.debug("Node lifecycle service starting");

		topic.addMessageListener(new DistributedMessageListener());
		eventBus.register(this);

		logger.info("Node lifecycle service started");
	}

	private void internalStop(final FSM<State, Event> fsm) {
		logger.debug("Node lifecycle service stopping");

		logger.info("Destroying the node");
		eventBus.post(new NodeDestroyedEvent());

		logger.info("Node lifecycle service stopped");
	}

	private void connectionDown(final FSM<State, Event> fsm) {
		logger.debug("Connection down.");
	}

	private void reconnected(final FSM<State, Event> fsm) {
		logger.debug("Reconnected");
	}

	// Message handling

	private void handleDestroy(final @Nullable Serializable serializable) {
		assert serializable == null;
		logger.debug("Destroy message received");
		service.fire(Event.STOP);
	}

	// Listeners

	@Subscribe public void handleDiscoveryServiceStoppingEvent(final DiscoveryServiceStoppingEvent event) {
		logger.debug("Discovery service is stopping");
		service.fire(Event.CONNECTION_DOWN);
	}

	private class DistributedMessageListener implements MessageListener<LifecycleMessage> {
		@Override public void onMessage(final Message<LifecycleMessage> message) {
			logger.debug("Distributed event: {}", message);
			final LifecycleMessage lifecycleMessage = message.getMessageObject();
			logger.debug("Lifecycle message: {}", lifecycleMessage);
			messageHandlers.get(lifecycleMessage.type()).accept(lifecycleMessage.payload().orElse(null));
		}
	}

	private static class ExceptionHandler implements Consumer<Throwable> {
		@Override public void accept(final Throwable throwable) {
			assert throwable != null;
			logger.error("Node lifecycle service error", throwable);
		}
	}
}
