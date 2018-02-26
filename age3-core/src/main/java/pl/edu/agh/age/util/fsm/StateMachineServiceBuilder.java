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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newEnumMap;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.annotation.ForTestsOnly;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Consumer;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;

/**
 * A builder of {@link DefaultStateMachineService} instances. It offers a simple, flexible interface for creation of
 * state
 * machines.
 *
 * Initially, a user is required to provide at least:
 * <ul>
 * <li> an enumeration of states,
 * <li> an enumeration of transitions,
 * <li> an entry state ({@link #startWith}),
 * <li> terminal states ({@link #terminateIn}).
 * </ul>
 *
 * Failure to do so results in {@link IllegalStateException} when {@link #build} is called.
 *
 * @param <S>
 * 		the states enumeration.
 * @param <E>
 * 		the events enumeration.
 */
@SuppressWarnings({"ReturnOfInnerClass", "AssignmentOrReturnOfFieldWithMutableType"})
public final class StateMachineServiceBuilder<S extends Enum<S>, E extends Enum<E>> {

	private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceBuilder.class);

	private final Multimap<S, Transition<S, E>> transitions = HashMultimap.create();

	private final EnumMap<E, Tuple2<Set<S>, @Nullable Consumer<FSM<S, E>>>> wildcardTransitions;

	private final Class<S> stateClass;

	private final Class<E> eventClass;

	private @MonotonicNonNull S initialState = null;

	private @MonotonicNonNull EnumSet<S> terminalStates = null;

	private @MonotonicNonNull EventBus eventBus = null;

	private @MonotonicNonNull String name = null;

	private Consumer<Throwable> exceptionHandler = t -> {};

	private boolean synchronous = false;

	private StateMachineServiceBuilder(final Class<S> states, final Class<E> events) {
		stateClass = requireNonNull(states);
		eventClass = requireNonNull(events);
		wildcardTransitions = newEnumMap(eventClass);
	}

	// Builder methods

	public static <S extends Enum<S>, E extends Enum<E>> StateMachineServiceBuilder<S, E> withStatesAndEvents(
		final Class<S> states, final Class<E> events) {
		return new StateMachineServiceBuilder<>(states, events);
	}

	@EnsuresNonNull("this.name") public StateMachineServiceBuilder<S, E> withName(final String name) {
		this.name = requireNonNull(name);
		return this;
	}

	/**
	 * Starts the declaration of behaviour when the FSM is at the given state.
	 *
	 * @param state
	 * 		a state.
	 *
	 * @return an action builder.
	 */
	public TransitionBuilder in(final S state) {
		return new TransitionBuilder(state);
	}

	/**
	 * Starts the declaration of behaviour for the events that are not dependent on states.
	 *
	 * @return an action builder.
	 */
	public WildcardTransitionBuilder inAnyState() {
		return new WildcardTransitionBuilder();
	}

	/**
	 * Declares an initial state.
	 *
	 * @param state
	 * 		a state.
	 *
	 * @return this builder instance.
	 */
	@EnsuresNonNull("initialState") public StateMachineServiceBuilder<S, E> startWith(final S state) {
		initialState = requireNonNull(state);
		logger.debug("Starting state: {}", initialState);
		return this;
	}

	/**
	 * Indicates which states are terminal states.
	 *
	 * @param states
	 * 		states that should be marked as terminal states.
	 *
	 * @return this builder instance.
	 */
	@EnsuresNonNull("terminalStates") public StateMachineServiceBuilder<S, E> terminateIn(final S... states) {
		checkArgument(states.length > 0, "Must provide at least one terminating state");

		terminalStates = EnumSet.copyOf(Arrays.asList(states));
		logger.debug("Terminal states: {}", terminalStates);
		return this;
	}

	/**
	 * Starts the declaration of actions taken when the failure occurs.
	 *
	 * @return a failure behaviour builder.
	 */
	public StateMachineServiceBuilder<S, E> whenFailedCall(final Consumer<Throwable> exceptionHandler) {
		this.exceptionHandler = requireNonNull(exceptionHandler);
		return this;
	}

	@EnsuresNonNull("this.eventBus") public StateMachineServiceBuilder<S, E> notifyOn(final EventBus eventBus) {
		this.eventBus = requireNonNull(eventBus);
		return this;
	}

	/**
	 * Builds and returns a new service.
	 *
	 * @return a new {@code StateMachineService}.
	 */
	public StateMachineService<S, E> build() {
		checkState((name != null) && (initialState != null) && (terminalStates != null));
		logger.debug("Building a state machine: N={}, S={}, E={}", name, stateClass, eventClass);

		return new DefaultStateMachineService<>(this);
	}

	// Package-protected methods for service creation and testing

	Class<S> stateClass() {
		assert stateClass != null;
		return stateClass;
	}

	Class<E> eventClass() {
		assert eventClass != null;
		return eventClass;
	}

	String name() {
		assert name != null;
		return name;
	}

	Multimap<S, Transition<S, E>> transitions() {
		assert transitions != null;
		return transitions;
	}

	S initialState() {
		assert initialState != null;
		return initialState;
	}

	EnumSet<S> terminalStates() {
		assert terminalStates != null;
		return terminalStates;
	}

	@Nullable EventBus eventBus() {
		return eventBus;
	}

	EnumMap<E, Tuple2<Set<S>, @Nullable Consumer<FSM<S, E>>>> wildcardTransitions() {
		return wildcardTransitions;
	}

	Consumer<Throwable> exceptionHandler() {
		assert exceptionHandler != null;
		return exceptionHandler;
	}

	boolean isSynchronous() {
		return synchronous;
	}

	HashMap<S, State<S, E>> buildStatesMap() {
		HashMap<S, State<S, E>> map = HashMap.empty();

		for (final S state : EnumSet.allOf(stateClass)) {
			final Map<E, Transition<S, E>> wildcardTransitionMap = Stream.ofAll(wildcardTransitions.entrySet())
			                                                             .toMap(entry -> Tuple.of(entry.getKey(),
			                                                                                      new Transition<>(
				                                                                                      state,
				                                                                                      entry.getKey(),
				                                                                                      entry.getValue()._1,
				                                                                                      entry.getValue()._2)));

			final Map<E, Transition<S, E>> transitionsMap = Stream.ofAll(transitions.get(state))
			                                                      .toMap(t -> Tuple.of(t.event(), t));

			final Map<E, Transition<S, E>> hashMap = transitionsMap.merge(wildcardTransitionMap);
			map = map.put(state, new State<>(state, terminalStates.contains(state), hashMap));
		}

		if (logger.isDebugEnabled()) {
			map.values()
			   .flatMap(State::transitions)
			   .forEach(descriptor -> logger.debug("New transition: {}", descriptor));
		}

		return map;
	}

	// Methods used only by tests

	@ForTestsOnly void synchronous() {
		synchronous = true;
	}

	// Additional builders for transitions

	/**
	 * An action builder.
	 *
	 * @author AGH AgE Team
	 */
	@SuppressWarnings("NonStaticInnerClassInSecureContext")
	public final class TransitionBuilder {

		private final S entry;

		private @Nullable E event = null;

		private @Nullable Set<S> exitStates = null;

		private @Nullable Consumer<FSM<S, E>> action = null;

		private TransitionBuilder(final S entry) {
			assert entry != null;
			this.entry = entry;
		}

		/**
		 * Declares an event that causes the action.
		 *
		 * @param initiatingEvent
		 * 		a causing event.
		 *
		 * @return this action builder.
		 */
		public TransitionBuilder on(final E initiatingEvent) {
			checkState(event == null, "Transition cannot be redeclared");
			event = requireNonNull(initiatingEvent);
			return this;
		}

		/**
		 * Declares an action to be executed during transition.
		 *
		 * @param actionToExecute
		 * 		an action to execute.
		 *
		 * @return this action builder.
		 */
		public TransitionBuilder execute(final Consumer<FSM<S, E>> actionToExecute) {
			action = requireNonNull(actionToExecute);
			return this;
		}

		/**
		 * Declares a target state.
		 *
		 * @param state
		 * 		a target state.
		 *
		 * @return this action builder.
		 */
		@SafeVarargs public final TransitionBuilder goTo(final S... state) {
			requireNonNull(state);
			checkArgument(state.length > 0, "Empty set of targets");

			exitStates = HashSet.of(state);
			return this;
		}

		public TransitionBuilder and() {
			checkState(event != null, "Event not provided");
			checkState((exitStates != null) && !exitStates.isEmpty(), "Transition targets not provided");

			transitions.put(entry, new Transition<>(entry, event, HashSet.ofAll(exitStates), action));
			event = null;
			exitStates = null;
			action = null;
			return this;
		}

		/**
		 * Finishes the action declaration.
		 *
		 * @return a state machine builder.
		 */
		public StateMachineServiceBuilder<S, E> commit() {
			checkState(event != null, "Event not provided");
			checkState((exitStates != null) && !exitStates.isEmpty(), "Transition targets not provided");

			transitions.put(entry, new Transition<>(entry, event, HashSet.ofAll(exitStates), action));
			return StateMachineServiceBuilder.this;
		}
	}

	/**
	 * An action builder for state-independent actions.
	 *
	 * @author AGH AgE Team
	 */
	@SuppressWarnings("NonStaticInnerClassInSecureContext")
	public final class WildcardTransitionBuilder {

		private @Nullable E event = null;

		private @Nullable Set<S> exitStates = null;

		private @Nullable Consumer<FSM<S, E>> action = null;

		/**
		 * Declares an event that causes the action.
		 *
		 * @param initiatingEvent
		 * 		a causing event.
		 *
		 * @return this action builder.
		 */
		public WildcardTransitionBuilder on(final E initiatingEvent) {
			checkState(event == null, "Transition cannot be redeclared");
			event = requireNonNull(initiatingEvent);
			return this;
		}

		/**
		 * Declares an action to be executed during transition.
		 *
		 * @param actionToExecute
		 * 		an action to execute.
		 *
		 * @return this action builder.
		 */
		public WildcardTransitionBuilder execute(final Consumer<FSM<S, E>> actionToExecute) {
			action = requireNonNull(actionToExecute);
			return this;
		}

		/**
		 * Declares a target state.
		 *
		 * @param state
		 * 		a target state.
		 *
		 * @return this action builder.
		 */
		@SafeVarargs public final WildcardTransitionBuilder goTo(final S... state) {
			requireNonNull(state);
			checkArgument(state.length > 0, "Empty set of targets");

			exitStates = HashSet.of(state);
			return this;
		}

		public WildcardTransitionBuilder and() {
			checkState(event != null, "Event not provided");
			checkState((exitStates != null) && !exitStates.isEmpty(), "Transition targets not provided");

			wildcardTransitions.put(event, Tuple.of(exitStates, action));
			event = null;
			exitStates = null;
			action = null;
			return this;
		}

		/**
		 * Finishes the action declaration.
		 *
		 * @return a state machine builder.
		 */
		public StateMachineServiceBuilder<S, E> commit() {
			checkState(event != null, "Event not provided");
			checkState((exitStates != null) && !exitStates.isEmpty(), "Transition targets not provided");

			wildcardTransitions.put(event, Tuple.of(exitStates, action));
			return StateMachineServiceBuilder.this;
		}
	}

}
