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
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.lifecycle.NodeDestroyedEvent;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;
import pl.edu.agh.age.services.topology.TopologyService;
import pl.edu.agh.age.services.worker.TaskFailedEvent;
import pl.edu.agh.age.services.worker.TaskFinishedEvent;
import pl.edu.agh.age.services.worker.TaskStartedEvent;
import pl.edu.agh.age.services.worker.WorkerMessage;
import pl.edu.agh.age.services.worker.WorkerService;
import pl.edu.agh.age.services.worker.internal.task.NullTask;
import pl.edu.agh.age.services.worker.internal.task.Task;
import pl.edu.agh.age.services.worker.internal.task.TaskBuilder;
import pl.edu.agh.age.util.fsm.FSM;
import pl.edu.agh.age.util.fsm.StateMachineService;
import pl.edu.agh.age.util.fsm.StateMachineServiceBuilder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

public final class DefaultWorkerService implements SmartLifecycle, WorkerCommunication, WorkerService {

	public enum State {
		OFFLINE,
		RUNNING,
		CONFIGURED,
		EXECUTING,
		PAUSED,
		FINISHED,
		COMPUTATION_CANCELED,
		COMPUTATION_FAILED,
		FAILED,
		TERMINATED
	}

	public enum Event {
		START,
		CONFIGURE,
		START_EXECUTION,
		PAUSE_EXECUTION,
		RESUME_EXECUTION,
		CANCEL_EXECUTION,
		COMPUTATION_FINISHED,
		COMPUTATION_FAILED,
		CLEAN,
		ERROR,
		TERMINATE
	}

	public enum ConfigurationKey {
		CONFIGURATION,
		COMPUTATION_STATE;
	}

	public static final String CHANNEL_NAME = "worker/channel";

	public static final String CONFIGURATION_MAP_NAME = "worker/config";

	public static final String STATE_MAP_NAME = "worker/state";

	private static final Logger log = LoggerFactory.getLogger(DefaultWorkerService.class);

	private final ListeningScheduledExecutorService executorService = listeningDecorator(newScheduledThreadPool(5));

	private final Map<WorkerMessage.Type, Set<CommunicationFacility>> workerMessageListeners = newEnumMap(
			WorkerMessage.Type.class);

	private final Set<CommunicationFacility> communicationFacilities = newHashSet();

	private final DefaultThreadPool computeThreadPool = new DefaultThreadPool();

	private HazelcastDistributionUtilities computeDistributionUtilities;

	private final EnumMap<WorkerMessage.Type, Consumer<Serializable>> messageHandlers = newEnumMap(
			WorkerMessage.Type.class);

	@Inject private HazelcastInstance hazelcastInstance;

	@Inject private NodeIdentityService identityService;

	@Inject private NodeLifecycleService lifecycleService;

	@Inject @Named("default") private TopologyService topologyService;

	@Inject private EventBus eventBus;

	@Inject private ApplicationContext applicationContext;

	private @MonotonicNonNull ITopic<WorkerMessage<Serializable>> topic;

	private @MonotonicNonNull Map<ConfigurationKey, Object> configurationMap;

	private @MonotonicNonNull IMap<String, ComputationState> nodeComputationState;

	private @MonotonicNonNull StateMachineService<State, Event> service;

	private @Nullable TaskBuilder taskBuilder;

	private Task currentTask = NullTask.INSTANCE;

	private DefaultWorkerService() {
		Arrays.stream(WorkerMessage.Type.values()).forEach(type -> workerMessageListeners.put(type, newHashSet()));

		messageHandlers.put(WorkerMessage.Type.LOAD_CONFIGURATION, payload -> service.fire(Event.CONFIGURE));
		messageHandlers.put(WorkerMessage.Type.START_COMPUTATION, payload -> service.fire(Event.START_EXECUTION));
		messageHandlers.put(WorkerMessage.Type.STOP_COMPUTATION, payload -> service.fire(Event.CANCEL_EXECUTION));
		messageHandlers.put(WorkerMessage.Type.CLEAN_CONFIGURATION, payload -> service.fire(Event.CLEAN));
	}

	@PostConstruct private void construct() {
		//@formatter:off
		service = StateMachineServiceBuilder
			.withStatesAndEvents(State.class, Event.class)
			.withName("worker")
			.startWith(State.OFFLINE)
			.terminateIn(State.TERMINATED, State.FAILED)

			.in(State.OFFLINE)
				.on(Event.START).execute(this::internalStart).goTo(State.RUNNING)
				.commit()

			.in(State.RUNNING)
				.on(Event.CONFIGURE).execute(this::configure).goTo(State.CONFIGURED)
				.commit()

			.in(State.CONFIGURED)
				.on(Event.START_EXECUTION).execute(this::startTask).goTo(State.EXECUTING, State.CONFIGURED)
				.on(Event.CANCEL_EXECUTION).execute(this::cancelTask).goTo(State.COMPUTATION_CANCELED)
				.commit()

			.in(State.EXECUTING)
				.on(Event.PAUSE_EXECUTION).execute(this::pauseTask).goTo(State.PAUSED)
				.on(Event.CANCEL_EXECUTION).execute(this::cancelTask).goTo(State.COMPUTATION_CANCELED)
				.on(Event.COMPUTATION_FAILED).execute(this::taskFailed).goTo(State.COMPUTATION_FAILED)
				.on(Event.COMPUTATION_FINISHED).execute(this::taskFinished).goTo(State.FINISHED)
			.commit()

			.in(State.PAUSED)
				.on(Event.RESUME_EXECUTION).execute(this::resumeTask).goTo(State.EXECUTING)
				.on(Event.CANCEL_EXECUTION).execute(this::cancelTask).goTo(State.COMPUTATION_CANCELED)
				.on(Event.COMPUTATION_FAILED).goTo(State.COMPUTATION_FAILED)
				.on(Event.COMPUTATION_FINISHED).goTo(State.FINISHED)
			.commit()

			.in(State.FINISHED)
				.on(Event.CLEAN).execute(this::cleanUpAfterTask).goTo(State.RUNNING)
			.commit()

			.inAnyState()
				.on(Event.TERMINATE).execute(this::terminate).goTo(State.TERMINATED)
				.on(Event.ERROR).execute(this::handleError).goTo(State.FAILED)
				.commit()

			.ifFailed()
				.fireAndCall(Event.ERROR, new ExceptionHandler())

			.withEventBus(eventBus)
			.build();
		//@formatter:on

		log.debug("Hazelcast instance: {}", hazelcastInstance);
		topic = hazelcastInstance.getTopic(CHANNEL_NAME);
		topic.addMessageListener(new DistributedMessageListener());
		configurationMap = hazelcastInstance.getMap(CONFIGURATION_MAP_NAME);
		nodeComputationState = hazelcastInstance.getMap(STATE_MAP_NAME);
		eventBus.register(this);
		computeDistributionUtilities = new HazelcastDistributionUtilities(hazelcastInstance);
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
		log.debug("Sending message {}.", message);
		topic.publish(message);
	}

	@Override
	public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
	                                                        final long period, final TimeUnit unit) {
		return executorService.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	// State changes

	private void internalStart(final FSM<State, Event> fsm) {
		log.debug("Worker service starting.");

		setNodeComputationState(ComputationState.NONE);

		// Catch up to other nodes if computation is running
		if (globalComputationState() == ComputationState.CONFIGURED) {
			service.fire(Event.CONFIGURE);
		}

		if (globalComputationState() == ComputationState.RUNNING) {
			service.fire(Event.CONFIGURE);
			service.fire(Event.START_EXECUTION);
		}

		log.info("Worker service started.");
	}

	private void terminate(final FSM<State, Event> fsm) {
		log.debug("Topology service stopping.");
		shutdownAndAwaitTermination(executorService, 10L, TimeUnit.SECONDS);
		log.info("Topology service stopped.");
	}

	private void handleError(final FSM<State, Event> fsm) {

	}

	private void configure(final FSM<State, Event> fsm) {
		assert !isTaskPresent() : "Task is already configured.";

		final WorkerConfiguration configuration = (WorkerConfiguration)configurationMap.get(
				ConfigurationKey.CONFIGURATION);
		final TaskBuilder classTaskBuilder = configuration.taskBuilder();
		prepareContext(classTaskBuilder);
		taskBuilder = classTaskBuilder;
		setNodeComputationState(ComputationState.CONFIGURED);
		changeGlobalComputationStateIfMaster(ComputationState.CONFIGURED);
	}

	private void startTask(final FSM<State, Event> fsm) {
		assert nonNull(taskBuilder);

		if (!isEnvironmentReady()) {
			log.warn("Trying to start computation when node is not ready.");
			// Reschedule the event once again
			executorService.schedule(() -> service.fire(Event.START_EXECUTION), 1L, TimeUnit.SECONDS);
			fsm.goTo(State.CONFIGURED);
			return;
		}

		log.debug("Starting task {}.", taskBuilder);

		communicationFacilities.forEach(CommunicationFacility::start);
		currentTask = taskBuilder.buildAndSchedule(executorService, new ExecutionListener());
		eventBus.post(new TaskStartedEvent());
		setNodeComputationState(ComputationState.RUNNING);
		changeGlobalComputationStateIfMaster(ComputationState.RUNNING);
		fsm.goTo(State.EXECUTING);
	}

	private void pauseTask(final FSM<State, Event> fsm) {
		log.debug("Pausing current task {}.", currentTask);
		currentTask.pause();
	}

	private void resumeTask(final FSM<State, Event> fsm) {
		log.debug("Resuming current task {}.", currentTask);
		currentTask.resume();
	}

	private void cancelTask(final FSM<State, Event> fsm) {
		log.debug("Cancelling current task {}.", currentTask);
		currentTask.cancel();
	}

	private void stopTask(final FSM<State, Event> fsm) {
		log.debug("Stopping current task {}.", currentTask);
		currentTask.stop();
	}

	private void taskFinished(final FSM<State, Event> fsm) {
		setNodeComputationState(ComputationState.FINISHED);
		final Collection<ComputationState> states = nodeComputationState.values(
				v -> v.getValue() != ComputationState.FINISHED);
		if (states.isEmpty()) {
			log.debug("All nodes finished computation.");
			changeGlobalComputationStateIfMaster(ComputationState.FINISHED);
		}
	}

	private void taskFailed(final FSM<State, Event> fsm) {
		changeGlobalComputationStateIfMaster(ComputationState.FAILED);
	}

	private void cleanUpAfterTask(final FSM<State, Event> fsm) {
		log.debug("Cleaning up after task {}.", currentTask);
		currentTask.cleanUp();
		currentTask = NullTask.INSTANCE;
		setNodeComputationState(ComputationState.NONE);
		changeGlobalComputationStateIfMaster(ComputationState.NONE);
		log.debug("Clean up finished.");
	}

	private boolean isTaskPresent() {
		return nonNull(taskBuilder) && !currentTask.equals(NullTask.INSTANCE);
	}

	private boolean isEnvironmentReady() {
		return lifecycleService.isRunning() && topologyService.hasTopology();
	}

	private ComputationState globalComputationState() {
		return configurationValue(ConfigurationKey.COMPUTATION_STATE, ComputationState.class).orElseGet(
				() -> ComputationState.NONE);
	}

	private ComputationState nodeComputationState() {
		return nodeComputationState.get(identityService.nodeId());
	}

	private void setNodeComputationState(final ComputationState state) {
		assert nonNull(state);
		nodeComputationState.set(identityService.nodeId(), state);
	}

	private <T> Optional<T> configurationValue(final ConfigurationKey key, final Class<T> klass) {
		return Optional.ofNullable((T)configurationMap.get(key));
	}

	private void changeGlobalComputationStateIfMaster(final ComputationState state) {
		assert nonNull(state);

		if (topologyService.isLocalNodeMaster()) {
			configurationMap.put(ConfigurationKey.COMPUTATION_STATE, state);
		}
	}

	private void prepareContext(final TaskBuilder taskBuilder) {
		assert nonNull(taskBuilder);

		// Configure communication facilities (as singletons)
		final Map<String, CommunicationFacility> facilitiesMap = applicationContext.getBeansOfType(
				CommunicationFacility.class);
		communicationFacilities.addAll(facilitiesMap.values());
		// Add services injected by the container
		log.debug("Registering facilities and adding them as listeners for messages.");
		communicationFacilities.forEach(service -> {
			service.subscribedTypes().forEach(key -> workerMessageListeners.get(key).add(service));
			taskBuilder.registerSingleton(service);
		});
		taskBuilder.registerSingleton(computeThreadPool);
		taskBuilder.registerSingleton(computeDistributionUtilities);

		// Refreshing the Spring context
		taskBuilder.finishConfiguration();
	}

	// Event bus handlers

	@Subscribe public void handleNodeDestroyedEvent(final NodeDestroyedEvent event) {
		log.debug("Got event: {}.", event);
		service.fire(Event.TERMINATE);
	}

	private final class DistributedMessageListener implements MessageListener<WorkerMessage<Serializable>> {

		@Override public void onMessage(final Message<WorkerMessage<Serializable>> message) {
			final WorkerMessage<Serializable> workerMessage = requireNonNull(message.getMessageObject());
			log.debug("WorkerMessage received: {}.", workerMessage);

			try {
				if (!workerMessage.isRecipient(identityService.nodeId())) {
					log.debug("Message {} was not directed to me.", workerMessage);
					return;
				}

				final WorkerMessage.Type type = workerMessage.type();
				final Set<CommunicationFacility> listeners = workerMessageListeners.get(type);
				boolean eaten = false;
				for (final CommunicationFacility listener : listeners) {
					log.debug("Notifying listener {}.", listener);
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
				log.info("T", t);
			}
		}
	}

	private final class ExecutionListener implements FutureCallback<Object> {

		@Override public void onSuccess(final Object result) {
			log.info("Task {} finished.", currentTask);
			eventBus.post(new TaskFinishedEvent());
			service.fire(Event.COMPUTATION_FINISHED);
		}

		@Override public void onFailure(final Throwable t) {
			if (t instanceof CancellationException) {
				log.debug("Task {} was cancelled. Ignoring exception.", currentTask);
				service.fire(Event.COMPUTATION_FAILED);
			} else {
				log.error("Task {} failed with error.", currentTask, t);
				eventBus.post(new TaskFailedEvent(t));
				service.fire(Event.COMPUTATION_FAILED);
			}
		}
	}

	private static final class ExceptionHandler implements Consumer<Throwable> {

		@Override public void accept(final Throwable throwable) {
			log.error("Exception", throwable);
		}
	}
}
