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

package pl.edu.agh.age.services.worker.internal;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import pl.edu.agh.age.services.topology.TopologyService;
import pl.edu.agh.age.services.worker.FailedComputationSetupException;
import pl.edu.agh.age.services.worker.TaskFailedEvent;
import pl.edu.agh.age.services.worker.TaskFinishedEvent;
import pl.edu.agh.age.services.worker.TaskStartedEvent;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;
import pl.edu.agh.age.services.worker.internal.task.ComputationContext;
import pl.edu.agh.age.util.fsm.FSM;
import pl.edu.agh.age.util.fsm.StateMachineService;
import pl.edu.agh.age.util.fsm.StateMachineServiceBuilder;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.IMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class ComputationService {

	private static final Logger logger = LoggerFactory.getLogger(ComputationService.class);

	public enum State {
		STOPPED,
		CONFIGURED,
		EXECUTING,
		PAUSED,
		FINISHED,
		CANCELLED,
		FAILED,
		TERMINATED
	}

	public enum Event {
		CONFIGURE,
		START,
		PAUSE,
		RESUME,
		CANCEL,
		CLEAN,
		FINISHED_SUCCESSFULLY,
		FAILED,
		TERMINATE,
	}

	private final StateMachineService<State, Event> service;

	private final Map<HazelcastObjectNames.ConfigurationKey, Object> configurationMap;

	private final ListeningScheduledExecutorService executorService = listeningDecorator(newScheduledThreadPool(5));

	private final EventBus eventBus;

	private final IMap<String, ComputationState> nodeComputationState;

	private final HazelcastDistributionUtilities computeDistributionUtilities;

	private final String nodeId;

	private final Set<CommunicationFacility> communicationFacilities;

	private final TopologyService topologyService;

	private @Nullable ComputationContext computationContext = null;

	ComputationService(final Map<HazelcastObjectNames.ConfigurationKey, Object> configurationMap,
	                   final EventBus eventBus,
	                   final IMap<String, ComputationState> nodeComputationState,
	                   final HazelcastDistributionUtilities computeDistributionUtilities, final String nodeId,
	                   final Set<CommunicationFacility> communicationFacilities,
	                   final TopologyService topologyService) {

		this.configurationMap = configurationMap;
		this.eventBus = eventBus;
		this.nodeComputationState = nodeComputationState;
		this.computeDistributionUtilities = computeDistributionUtilities;
		this.nodeId = nodeId;
		this.communicationFacilities = communicationFacilities;
		this.topologyService = topologyService;

		setNodeComputationState(ComputationState.NONE);

		//@formatter:off
		service = StateMachineServiceBuilder
			.withStatesAndEvents(State.class, Event.class)
			.withName("compute")
			.startWith(State.STOPPED)
			.terminateIn(State.TERMINATED)

			.in(State.STOPPED)
				.on(Event.CONFIGURE).execute(this::configure).goTo(State.CONFIGURED)
				.commit()

			.in(State.CONFIGURED)
				.on(Event.START).execute(this::startTask).goTo(State.EXECUTING, State.FAILED).and()
				.on(Event.CLEAN).execute(this::cleanUpAfterTask).goTo(State.STOPPED)
				.commit()

			.in(State.EXECUTING)
				.on(Event.PAUSE).execute(s -> computationContext.pause()).goTo(State.PAUSED).and()
				.on(Event.CANCEL).execute(s -> computationContext.cancel()).goTo(State.CANCELLED).and()
				.on(Event.FAILED).execute(this::taskFailed).goTo(State.FAILED).and()
				.on(Event.FINISHED_SUCCESSFULLY).execute(this::taskFinished).goTo(State.FINISHED)
				.commit()

			.in(State.PAUSED)
				.on(Event.RESUME).execute(s -> computationContext.resume()).goTo(State.EXECUTING).and()
				.on(Event.CANCEL).execute(s -> computationContext.cancel()).goTo(State.CANCELLED).and()
				.on(Event.FAILED).goTo(State.FAILED).and()
				.on(Event.FINISHED_SUCCESSFULLY).goTo(State.FINISHED)
				.commit()

			.in(State.FINISHED)
				.on(Event.CLEAN).execute(this::cleanUpAfterTask).goTo(State.STOPPED)
				.commit()

			.in(State.FAILED)
				// Must be CLEAN-ed
				.on(Event.CLEAN).execute(this::cleanUpAfterTask).goTo(State.STOPPED)
                .commit()

            .in(State.CANCELLED)
				.on(Event.CLEAN).execute(this::cleanUpAfterTask).goTo(State.STOPPED)
                .commit()

			.inAnyState()
				.on(Event.TERMINATE).execute(this::terminate).goTo(State.TERMINATED)
				.commit()

			.whenFailedCall(new ExceptionHandler())
			.notifyOn(this.eventBus)
			.build();
		//@formatter:on


		// Catch up to other nodes if computation is running
		if (globalComputationState() == ComputationState.CONFIGURED) {
			service.fire(Event.CONFIGURE);
		}

		if (globalComputationState() == ComputationState.RUNNING) {
			service.fire(Event.CONFIGURE);
			service.fire(Event.START);
		}
	}

	public void triggerConfigurationLoad() {
		service.fire(Event.CONFIGURE);
	}

	public void triggerStart() {
		service.fire(Event.START);
	}

	public void triggerCancel() {
		service.fire(Event.CANCEL);
	}

	public void triggerClear() {
		service.fire(Event.CLEAN);
	}

	public void triggerTermination() {
		service.fire(Event.TERMINATE);
	}

	// Transition actions

	private void configure(final FSM<State, Event> fsm) {
		assert computationContext == null : "Task is already configured.";

		final WorkerConfiguration configuration = (WorkerConfiguration)configurationMap.get(
			HazelcastObjectNames.ConfigurationKey.CONFIGURATION);
		computationContext = new ComputationContext(configuration, communicationFacilities,
		                                            computeDistributionUtilities);
		setNodeComputationState(ComputationState.CONFIGURED);
		changeGlobalComputationStateIfMaster(ComputationState.CONFIGURED);
	}

	private void startTask(final FSM<State, Event> fsm) {
		assert computationContext != null;

		logger.debug("Starting computation {}", computationContext);
		try {
			computationContext.startTask(executorService, new ExecutionListener());
			eventBus.post(new TaskStartedEvent());
			setNodeComputationState(ComputationState.RUNNING);
			changeGlobalComputationStateIfMaster(ComputationState.RUNNING);
			fsm.goTo(State.EXECUTING);
		} catch (final FailedComputationSetupException e) {
			logger.error("Computation could not be started", e);
			changeGlobalComputationStateIfMaster(ComputationState.FAILED);
			changeErrorIfMaster(e);
			fsm.goTo(State.FAILED);
		}
	}

	private void taskFinished(final FSM<State, Event> fsm) {
		setNodeComputationState(ComputationState.FINISHED);
		final Collection<ComputationState> states = nodeComputationState.values(
			v -> !v.getValue().equals(ComputationState.FINISHED));
		if (states.isEmpty()) {
			logger.debug("All nodes finished computation");
			changeGlobalComputationStateIfMaster(ComputationState.FINISHED);
		}
	}

	private void taskFailed(final FSM<State, Event> fsm) {
		changeGlobalComputationStateIfMaster(ComputationState.FAILED);
	}

	private void cleanUpAfterTask(final FSM<State, Event> fsm) {
		assert computationContext != null;

		logger.debug("Cleaning up");

		if (computationContext.isTaskActive()) {
			computationContext.cleanUp();
		}
		computationContext = null;
		setNodeComputationState(ComputationState.NONE);
		changeGlobalComputationStateIfMaster(ComputationState.NONE);
		changeErrorIfMaster(null);
		logger.debug("Clean up finished");
	}

	private void terminate(final FSM<State, Event> fsm) {
		logger.debug("Computation service stopping");
		shutdownAndAwaitTermination(executorService, 10L, TimeUnit.SECONDS);
		logger.info("Computation service stopped");
	}

	private <T> Optional<T> configurationValue(final HazelcastObjectNames.ConfigurationKey key, final Class<T> klass) {
		return Optional.ofNullable((T)configurationMap.get(key));
	}


	private ComputationState globalComputationState() {
		return configurationValue(HazelcastObjectNames.ConfigurationKey.COMPUTATION_STATE,
		                          ComputationState.class).orElse(ComputationState.NONE);
	}

	private ComputationState nodeComputationState() {
		return nodeComputationState.get(nodeId);
	}

	private void setNodeComputationState(final ComputationState state) {
		assert state != null;
		nodeComputationState.set(nodeId, state);
	}

	private void changeGlobalComputationStateIfMaster(final ComputationState state) {
		assert state != null;

		if (topologyService.isLocalNodeMaster()) {
			configurationMap.put(HazelcastObjectNames.ConfigurationKey.COMPUTATION_STATE, state);
		}
	}

	private void changeErrorIfMaster(final @Nullable Throwable error) {
		if (topologyService.isLocalNodeMaster()) {
			if (error == null) {
				configurationMap.remove(HazelcastObjectNames.ConfigurationKey.ERROR);
			} else {
				configurationMap.put(HazelcastObjectNames.ConfigurationKey.ERROR, error);
			}
		}
	}

	private final class ExecutionListener implements FutureCallback<Object> {

		@Override public void onSuccess(final Object result) {
			logger.info("Task {} finished", computationContext.currentTaskDescription());
			eventBus.post(new TaskFinishedEvent());
			service.fire(Event.FINISHED_SUCCESSFULLY);
		}

		@Override public void onFailure(final Throwable t) {
			if (t instanceof CancellationException) {
				logger.debug("Task {} was cancelled. Ignoring exception", computationContext.currentTaskDescription());
			} else {
				logger.error("Task {} failed with error", computationContext.currentTaskDescription(), t);
				eventBus.post(new TaskFailedEvent(t));
			}
			changeErrorIfMaster(t);
			service.fire(Event.FAILED);
		}
	}

	private static final class ExceptionHandler implements Consumer<Throwable> {
		@Override public void accept(final Throwable throwable) {
			logger.error("Internal ComputationService error", throwable);
		}
	}
}
