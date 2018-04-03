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

import static com.google.common.collect.Maps.newEnumMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.lifecycle.NodeDestroyedEvent;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;
import pl.edu.agh.age.services.topology.TopologyService;
import pl.edu.agh.age.services.worker.WorkerMessage;
import pl.edu.agh.age.services.worker.WorkerService;
import pl.edu.agh.age.util.fsm.FSM;
import pl.edu.agh.age.util.fsm.StateMachineService;
import pl.edu.agh.age.util.fsm.StateMachineServiceBuilder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("FeatureEnvy")
@Named
public final class DefaultWorkerService implements SmartLifecycle, WorkerCommunication, WorkerService {

	public enum State {
		OFFLINE,
		RUNNING,
		TERMINATED
	}

	public enum Event {
		START,
		TERMINATE,
	}

	private static final Logger logger = LoggerFactory.getLogger(DefaultWorkerService.class);

	private final ListeningScheduledExecutorService executorService = listeningDecorator(newScheduledThreadPool(5));

	private final Map<WorkerMessage.Type, Set<CommunicationFacility>> workerMessageListeners = newEnumMap(
		WorkerMessage.Type.class);

	private final Set<CommunicationFacility> communicationFacilities = newHashSet();

	private final Map<WorkerMessage.Type, Consumer<Serializable>> messageHandlers = newEnumMap(
		WorkerMessage.Type.class);

	private final NodeIdentityService identityService;

	private final NodeLifecycleService lifecycleService;

	private final TopologyService topologyService;

	private final ApplicationContext applicationContext;

	private final ITopic<WorkerMessage<Serializable>> topic;

	private final StateMachineService<State, Event> service;

	private final ComputationService computationService;

	@Inject
	private DefaultWorkerService(final NodeIdentityService identityService, final ApplicationContext applicationContext,
	                             final HazelcastInstance hazelcastInstance, final TopologyService topologyService,
	                             final EventBus eventBus, final NodeLifecycleService lifecycleService) {
		Arrays.stream(WorkerMessage.Type.values()).forEach(type -> workerMessageListeners.put(type, newHashSet()));

		this.identityService = identityService;
		this.applicationContext = applicationContext;
		this.topologyService = topologyService;
		this.lifecycleService = lifecycleService;

		//@formatter:off
		service = StateMachineServiceBuilder
			.withStatesAndEvents(State.class, Event.class)
			.withName("worker")
			.startWith(State.OFFLINE)
			.terminateIn(State.TERMINATED)

			.in(State.OFFLINE)
				.on(Event.START).execute(this::internalStart).goTo(State.RUNNING)
				.commit()

			.inAnyState()
				.on(Event.TERMINATE).execute(this::terminate).goTo(State.TERMINATED)
				.commit()

			.whenFailedCall(new ExceptionHandler())
			.notifyOn(eventBus)
			.build();
		//@formatter:on

		logger.debug("Hazelcast instance: {}", hazelcastInstance);
		topic = hazelcastInstance.getTopic(HazelcastObjectNames.CHANNEL_NAME);
		topic.addMessageListener(new DistributedMessageListener());

		eventBus.register(this);

		final IMap<String, ComputationState> nodeComputationState = hazelcastInstance.getMap(
			HazelcastObjectNames.STATE_MAP_NAME);
		final HazelcastDistributionUtilities computeDistributionUtilities = new HazelcastDistributionUtilities(
			hazelcastInstance);
		final String nodeId = identityService.nodeId();
		final IMap<HazelcastObjectNames.ConfigurationKey, Object> configurationMap = hazelcastInstance.getMap(
			HazelcastObjectNames.CONFIGURATION_MAP_NAME);

		computationService = new ComputationService(configurationMap, eventBus, nodeComputationState,
		                                            computeDistributionUtilities, nodeId, communicationFacilities,
		                                            topologyService);

		messageHandlers.put(WorkerMessage.Type.LOAD_CONFIGURATION,
		                    payload -> computationService.triggerConfigurationLoad());
		messageHandlers.put(WorkerMessage.Type.START_COMPUTATION, payload -> computationService.triggerStart());
		messageHandlers.put(WorkerMessage.Type.CANCEL_COMPUTATION, payload -> computationService.triggerCancel());
		messageHandlers.put(WorkerMessage.Type.CLEAN_CONFIGURATION, payload -> computationService.triggerClear());
	}

	@Override public boolean isAutoStartup() {
		return true;
	}

	@Override public void stop(final Runnable callback) {
		stop();
		callback.run();
	}

	@Override public void start() {
		service.fire(Event.START);
	}

	@Override public void stop() {
		service.fire(Event.TERMINATE);
	}

	@Override public boolean isRunning() {
		return service.isRunning();
	}

	@Override public int getPhase() {
		return Integer.MAX_VALUE;
	}

	@Override public void sendMessage(final WorkerMessage<Serializable> message) {
		logger.debug("Sending message {}", message);
		topic.publish(message);
	}

	@Override
	public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
	                                                        final long period, final TimeUnit unit) {
		return executorService.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	//
	// State changes - these must be called within FSM service.
	//

	private void internalStart(final FSM<State, Event> fsm) {
		logger.debug("Worker service is starting");

		try {
			while (!isEnvironmentReady()) {
				logger.warn("Trying to start computation when node is not ready");
				// Reschedule the event once again
				TimeUnit.SECONDS.sleep(1L);
			}
		} catch (final InterruptedException ignored) {
			if (!isEnvironmentReady()) {
				Thread.currentThread().interrupt();
				service.failWithError(new RuntimeException("Interrupted when waiting for the environment"));
			}
		}

		// Configure communication facilities (as singletons)
		final Map<String, CommunicationFacility> facilitiesMap = applicationContext.getBeansOfType(
			CommunicationFacility.class);
		communicationFacilities.addAll(facilitiesMap.values());
		// Add services injected by the container
		logger.debug("Registering facilities and adding them as listeners for messages");
		communicationFacilities.forEach(facility -> {
			facility.subscribedTypes().forEach(key -> workerMessageListeners.get(key).add(facility));
			facility.start();
		});

		logger.info("Worker service started");
	}

	private void terminate(final FSM<State, Event> fsm) {
		logger.debug("Worker service is stopping");
		computationService.triggerTermination();
		shutdownAndAwaitTermination(executorService, 10L, TimeUnit.SECONDS);
		logger.info("Worker service stopped");
	}

	// Utilities

	private boolean isEnvironmentReady() {
		return lifecycleService.isRunning() && topologyService.hasTopology();
	}

	// Event bus handlers

	@Subscribe public void handleNodeDestroyedEvent(final NodeDestroyedEvent event) {
		logger.debug("Got event: {}", event);
		service.fire(Event.TERMINATE);
	}

	private final class DistributedMessageListener implements MessageListener<WorkerMessage<Serializable>> {

		@Override public void onMessage(final Message<WorkerMessage<Serializable>> message) {
			final WorkerMessage<Serializable> workerMessage = requireNonNull(message.getMessageObject());
			logger.debug("WorkerMessage received: {}", workerMessage);

			try {
				if (!workerMessage.isRecipient(identityService.nodeId())) {
					logger.debug("Message {} was not directed to me", workerMessage);
					return;
				}

				final WorkerMessage.Type type = workerMessage.type();
				final Set<CommunicationFacility> listeners = workerMessageListeners.get(type);
				boolean eaten = false;
				for (final CommunicationFacility listener : listeners) {
					logger.debug("Notifying listener {}", listener);
					if (listener.onMessage(workerMessage)) {
						eaten = true;
						break;
					}
				}

				if (eaten) {
					return;
				}

				messageHandlers.get(type).accept(workerMessage.payload().orElse(null));
			} catch (final Throwable t) {
				logger.error("Error when handling a message", t);
			}
		}
	}

	private static final class ExceptionHandler implements Consumer<Throwable> {
		@Override public void accept(final Throwable throwable) {
			logger.error("Internal DefaultWorkerService error", throwable);
		}
	}
}
