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

package pl.edu.agh.age.util.fsm;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.consumingIterable;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Queues.newConcurrentLinkedQueue;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static pl.edu.agh.age.util.Runnables.swallowingRunnable;

import pl.edu.agh.age.annotation.ForTestsOnly;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

import javax.annotation.concurrent.ThreadSafe;

import io.vavr.collection.HashMap;
import io.vavr.collection.Set;
import io.vavr.control.Option;

/**
 * A FSM-based service implementation.
 * <p>
 * These services should be built with {@link StateMachineServiceBuilder}.
 *
 * @param <S>
 * 		the states enumeration.
 * @param <E>
 * 		the events enumeration.
 *
 * @see StateMachineServiceBuilder
 */
@ThreadSafe
@SuppressWarnings("initialization")
public final class DefaultStateMachineService<S extends Enum<S>, E extends Enum<E>>
		implements StateMachineService<S, E> {

	private static final Logger log = LoggerFactory.getLogger(DefaultStateMachineService.class);

	private final String serviceName;

	private final S initialState;

	private final boolean synchronous;

	private final HashMap<S, State<S, E>> statesMap;

	private final Consumer<Throwable> exceptionHandler;

	private final Queue<Throwable> exceptions = newConcurrentLinkedQueue();

	private final Queue<E> eventQueue = newConcurrentLinkedQueue();

	private final StampedLock stateLock = new StampedLock();

	private final @Nullable ListeningScheduledExecutorService service;

	private final @Nullable ScheduledFuture<?> dispatcherFuture;

	private final @Nullable EventBus eventBus;

	@GuardedBy("stateLock") private volatile boolean failed = false;

	@GuardedBy("stateLock") private volatile boolean terminated = false;

	@GuardedBy("stateLock") private State<S, E> currentState;

	@Nullable @GuardedBy("stateLock") private E currentEvent;

	@Nullable @GuardedBy("stateLock") private S nextState;

	/**
	 * Package-protected constructor.
	 * <p>
	 * <p>
	 * The proper way to build the service is to use the builder {@link StateMachineServiceBuilder}.
	 *
	 * @param builder
	 * 		a builder containing the state machine definition.
	 */
	DefaultStateMachineService(final StateMachineServiceBuilder<S, E> builder) {
		serviceName = builder.name();
		initialState = builder.initialState();
		statesMap = builder.buildStatesMap();
		final Option<State<S, E>> initialOption = statesMap.get(initialState);
		assert initialOption.isDefined() : "Initial state could not be found";
		currentState = initialOption.get();
		eventBus = builder.eventBus();
		exceptionHandler = builder.exceptionHandler();

		if (builder.isSynchronous()) {
			synchronous = true;
			service = null;
			dispatcherFuture = null;
		} else {
			synchronous = false;
			service = listeningDecorator(newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("fsm-" + serviceName + "-%d").build()));
			dispatcherFuture = service.scheduleWithFixedDelay(swallowingRunnable(new Dispatcher()), 0L, 1L,
			                                                  TimeUnit.MILLISECONDS);
		}
	}

	@Override public void fire(final E event) {
		log.debug("{}: {} fired", serviceName, event);
		logIfTerminated();
		eventQueue.add(event);
	}

	@Override public void goTo(final S state) {
		final long stamp = stateLock.writeLock();
		try {
			if (isRunningNotSynchronized()) {
				if (currentState.isNextState(state)) {
					nextState = state;
				}
			}
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public boolean isRunning() {
		final long stamp = stateLock.readLock();
		try {
			return isRunningNotSynchronized();
		} finally {
			stateLock.unlock(stamp);
		}
	}

	private boolean isRunningNotSynchronized() {
		return !currentState.is(initialState) && !terminated && !failed;
	}

	@Override public boolean isInState(final S state) {
		final long stamp = stateLock.readLock();
		try {
			return currentState.is(state);
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public boolean isTerminated() {
		final long stamp = stateLock.readLock();
		try {
			return terminated;
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public boolean isFailed() {
		final long stamp = stateLock.readLock();
		try {
			return failed;
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public void awaitTermination() throws InterruptedException {
		while (true) {
			final long stamp = stateLock.readLock();
			try {
				if (terminated) {
					return;
				}
			} finally {
				stateLock.unlock(stamp);
			}
			TimeUnit.SECONDS.sleep(1L);
		}
	}

	@Override public void shutdown() {
		checkState(isTerminated(), "Service has not terminated yet. Current state: %s", currentState());
		log.debug("{}: Service is in terminal state - performing shutdown", serviceName);
		internalTermination();
		internalShutdown();
	}

	@Override public void forceShutdown() {
		log.debug("{}: Performing force shutdown", serviceName);
		internalTermination();
		internalShutdown();
	}

	@Override public S currentState() {
		final long stamp = stateLock.readLock();
		try {
			return currentState.name();
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public void failWithError(final Throwable t) {
		final long stamp = stateLock.writeLock();
		try {
			log.warn("{}: Failed with error {}", serviceName, t.getMessage());
			failed = true;
			terminated = true;
			exceptions.add(t);
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Nullable public E currentEvent() {
		final long stamp = stateLock.readLock();
		try {
			return currentEvent;
		} finally {
			stateLock.unlock(stamp);
		}
	}

	@Override public String toString() {
		final long stamp = stateLock.readLock();
		try {
			return toStringHelper(this).addValue(serviceName)
			                           .add("S", currentState)
			                           .add("E", currentEvent)
			                           .add("failed?", failed)
			                           .add("terminated?", terminated)
			                           .toString();
		} finally {
			stateLock.unlock(stamp);
		}
	}

	void drainEvents() {
		for (final E event : consumingIterable(eventQueue)) {
			log.debug("{}: Unprocessed event {}", serviceName, event);
		}
	}

	@ForTestsOnly void execute() {
		assert synchronous;
		new Dispatcher().run();
	}

	private void internalTermination() {
		assert terminated;
		log.debug("{}: Service is terminating", serviceName);
		if (!synchronous) {
			assert (dispatcherFuture != null) && (service != null);
			dispatcherFuture.cancel(false);
		}
		drainEvents();
		log.info("{}: Service has been terminated", serviceName);
	}

	private void internalShutdown() {
		assert terminated && dispatcherFuture.isCancelled();
		log.debug("{}: Service is shutting down", serviceName);
		if (!synchronous) {
			shutdownAndAwaitTermination(service, 10L, TimeUnit.SECONDS);
		}
		log.info("{}: Service has been shut down", serviceName);
	}

	private void logIfTerminated() {
		final long stamp = stateLock.readLock();
		try {
			if (terminated) {
				log.warn("{}: Service already terminated ({})", serviceName, currentState);
			}
		} finally {
			stateLock.unlock(stamp);
		}
	}

	private final class Dispatcher implements Runnable {
		@Override public void run() {
			if (isTerminated() || isFailed()) {
				log.debug("{}: Already terminated or failed", serviceName);
				internalTermination();
				return;
			}

			final Transition<S, E> transition;
			long stamp = stateLock.readLock();
			try {
				// Still processing previous event
				if (nonNull(currentEvent)) {
					return;
				}
				final long writeStamp = stateLock.tryConvertToWriteLock(stamp);
				if (writeStamp == 0L) {
					stateLock.unlockRead(stamp);
					stamp = stateLock.writeLock();
				} else {
					stamp = writeStamp;
				}
				assert stamp != 0L;

				// Prepare the current event
				if (eventQueue.isEmpty()) {
					// Nothing to process
					return;
				}
				currentEvent = eventQueue.poll();
				// Process the current event
				log.debug("{}: In {} and processing {}", serviceName, currentState.name(), currentEvent);

				final Option<Transition<S, E>> transitionOption = currentState.transitionForEvent(currentEvent);

				if (transitionOption.isEmpty()) {
					// Ignore event
					return;
				}
				transition = transitionOption.get();
			} finally {
				stateLock.unlock(stamp);
			}

			assert transition != null;

			log.debug("{}: Planned transition: {}", serviceName, transition);

			// Execute the action
			try {
				final Consumer<FSM<S, E>> action = transition.action();
				log.debug("{}: Executing the planned action {}", serviceName, action);
				final String name = Thread.currentThread().getName();
				Thread.currentThread().setName("fsm-" + serviceName + "-" + transition.event());
				action.accept(DefaultStateMachineService.this);
				Thread.currentThread().setName(name);
				log.debug("{}: Finished the execution of the action", serviceName);
				onSuccess(transition);
			} catch (final Throwable t) {
				onFailure(transition, t);
			}

			consumingIterable(exceptions).forEach(exceptionHandler);
		}

		void onSuccess(final Transition<S, E> transition) {
			final long stamp = stateLock.writeLock();
			try {
				final Set<S> targetSet = transition.targets();
				if ((targetSet.size() != 1) && (nextState == null)) {
					log.error("{}: Transition {} did not set the target state. Possible states: {}", serviceName,
					          transition, targetSet);
					failed = true;
				} else {
					currentState = statesMap.get(((targetSet.size() != 1) && (nextState != null)) ? nextState
					                                                                : getOnlyElement(targetSet)).get();
					if (currentState.isTerminal()) {
						terminated = true;
					}
					log.debug("{}: Transition {} was successful. Selected state: {}", serviceName, transition,
					         currentState);
					if (eventBus != null) {
						eventBus.post(
							new StateChangedEvent<>(transition.initial(), transition.event(), currentState.name()));
					}
				}
			} finally {
				currentEvent = null;
				nextState = null;
				stateLock.unlock(stamp);
			}
		}

		void onFailure(final Transition<S, E> descriptor, final Throwable t) {
			final long stamp = stateLock.writeLock();
			try {
				log.error("{}: Transition {} failed with exception", serviceName, descriptor, t);
				failed = true;
				terminated = true;
				exceptions.add(t);
				currentEvent = null;
				nextState = null;
			} finally {
				stateLock.unlock(stamp);
			}
		}
	}

}
