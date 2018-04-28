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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

import one.util.streamex.StreamEx;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import io.vavr.Tuple2;
import io.vavr.collection.Set;

public final class StateMachineServiceBuilderTest {

	private StateMachineServiceBuilder<State, Event> builder;

	private enum State {
		STATE1,
		STATE2,
		STATE3
	}

	private enum Event {
		EVENT1,
		EVENT2,
		EVENT3
	}

	private static final String SERVICE_NAME = "name";

	private static final Consumer<FSM<State, Event>> consumer1 = fsm -> {};

	private static final Consumer<FSM<State, Event>> consumer2 = fsm -> {};

	@Before public void setUp() {
		builder = StateMachineServiceBuilder.withStatesAndEvents(State.class, Event.class);
	}

	@Test(expected = NullPointerException.class) public void testConstructor_nullEventsAndStates() {
		StateMachineServiceBuilder.<State, Event>withStatesAndEvents(null, null);

		failBecauseExceptionWasNotThrown(NullPointerException.class);
	}

	@Test public void testConstructor_correctInitialization() {
		assertThat(builder.stateClass()).isEqualTo(State.class);
		assertThat(builder.eventClass()).isEqualTo(Event.class);
		assertThat(builder.eventBus()).isNull();
	}

	@Test public void testNotifyOn() {
		final EventBus eventBus = new EventBus();
		builder.notifyOn(eventBus);
		assertThat(builder.eventBus()).isEqualTo(eventBus);
	}

	@Test public void testWithName() {
		builder.withName(SERVICE_NAME);
		assertThat(builder.name()).isEqualTo(SERVICE_NAME);
	}

	@Test public void testStateDefinition_singleEvent() {
		builder.in(State.STATE1).on(Event.EVENT1).execute(consumer1).goTo(State.STATE2).commit();

		final Multimap<State, Transition<State, Event>> transitions = builder.transitions();
		final Collection<Transition<State, Event>> stateTransitions = transitions.get(State.STATE1);

		assertThat(stateTransitions).isNotNull().hasSize(1);

		final Transition<State, Event> transition = getOnlyElement(stateTransitions);
		assertThat(transition).isNotNull();
		assertThat(transition.action()).isEqualTo(consumer1);
		assertThat(transition.targets()).isNotEmpty().contains(State.STATE2);
	}

	@Test public void testStateDefinition_multipleEvents() {
		//@formatter:off
		builder.in(State.STATE1)
					.on(Event.EVENT1)
					.execute(consumer1)
					.goTo(State.STATE2)
               .and()
					.on(Event.EVENT2)
					.execute(consumer2)
					.goTo(State.STATE3)
		       .commit();
		//@formatter:on

		final Multimap<State, Transition<State, Event>> transitions = builder.transitions();

		final Collection<Transition<State, Event>> stateTransitions = transitions.get(State.STATE1);

		assertThat(stateTransitions).isNotNull().hasSize(2);

		final Optional<Transition<State, Event>> e1transition = StreamEx.of(stateTransitions)
		                                                                .filter(t -> t.event() == Event.EVENT1)
		                                                                .findAny();
		final Optional<Transition<State, Event>> e2transition = StreamEx.of(stateTransitions)
		                                                                .filter(t -> t.event() == Event.EVENT2)
		                                                                .findAny();

		assertThat(e1transition).isNotEmpty()
		                        .get()
		                        .matches(t -> t.action().equals(consumer1))
		                        .matches(t -> t.targets().contains(State.STATE2));
		assertThat(e2transition).isNotEmpty()
		                        .get()
		                        .matches(t -> t.action().equals(consumer2))
		                        .matches(t -> t.targets().contains(State.STATE3));
	}

	@Test(expected = IllegalStateException.class) public void testStateDefinition_incorrectDefinition() {
		builder.in(State.STATE1)
		       .on(Event.EVENT1)
		       .execute(consumer1)
		       .on(Event.EVENT2)
		       .execute(consumer2)
		       .goTo(State.STATE3)
		       .commit();
	}

	@Test(expected = IllegalStateException.class) public void testStateDefinition_noTransitionProvided() {
		builder.in(State.STATE1).on(Event.EVENT1).commit();

		failBecauseExceptionWasNotThrown(IllegalStateException.class);
	}

	@Test public void testAnyStateDefinition_singleEvent() {
		builder.inAnyState().on(Event.EVENT1).execute(consumer1).goTo(State.STATE2).commit();

		final Map<Event, Tuple2<Set<State>, Consumer<FSM<State, Event>>>> transitions = builder.wildcardTransitions();

		final Tuple2<Set<State>, Consumer<FSM<State, Event>>> t2 = transitions.get(Event.EVENT1);

		final Set<State> states = t2._1;
		final Consumer<FSM<State, Event>> action = t2._2;

		assertThat(action).isNotNull();
		assertThat(action).isEqualTo(consumer1);
		assertThat(states).isNotEmpty();
		assertThat(states).contains(State.STATE2);
	}

	@Test public void testAnyStateDefinition_multipleEvents() {
		builder.inAnyState()
		       .on(Event.EVENT1)
		       .execute(consumer1)
		       .goTo(State.STATE2)
		       .and()
		       .on(Event.EVENT2)
		       .execute(consumer2)
		       .goTo(State.STATE3)
		       .commit();

		final Map<Event, Tuple2<Set<State>, Consumer<FSM<State, Event>>>> transitions = builder.wildcardTransitions();

		final Tuple2<Set<State>, Consumer<FSM<State, Event>>> t1 = transitions.get(Event.EVENT1);
		final Set<State> states1 = t1._1;
		final Consumer<FSM<State, Event>> action1 = t1._2;

		final Tuple2<Set<State>, Consumer<FSM<State, Event>>> t2 = transitions.get(Event.EVENT2);
		final Set<State> states2 = t2._1;
		final Consumer<FSM<State, Event>> action2 = t2._2;

		assertThat(action1).isNotNull();
		assertThat(action1).isEqualTo(consumer1);
		assertThat(states1).isNotEmpty();
		assertThat(states1).contains(State.STATE2);

		assertThat(action2).isNotNull();
		assertThat(action2).isEqualTo(consumer2);
		assertThat(states2).isNotEmpty();
		assertThat(states2).contains(State.STATE3);
	}

	@Test(expected = IllegalStateException.class) public void testAnyStateDefinition_incorrectDefinition() {
		builder.inAnyState()
		       .on(Event.EVENT1)
		       .execute(consumer1)
		       .on(Event.EVENT2)
		       .execute(consumer2)
		       .goTo(State.STATE3)
		       .commit();

		failBecauseExceptionWasNotThrown(IllegalStateException.class);
	}

	@Test(expected = IllegalStateException.class) public void testAnyStateDefinition_noTransitionProvided() {
		builder.inAnyState().on(Event.EVENT1).commit();

		failBecauseExceptionWasNotThrown(IllegalStateException.class);
	}

	@Test(expected = NullPointerException.class) public void testStartWith() {
		builder.startWith(State.STATE1);
		assertThat(builder.initialState()).isEqualTo(State.STATE1);

		builder.startWith(null);

		failBecauseExceptionWasNotThrown(NullPointerException.class);
	}

	@Test(expected = IllegalArgumentException.class) public void testTerminateIn() {
		builder.terminateIn(State.STATE1);

		assertThat(builder.terminalStates()).hasSize(1);
		assertThat(builder.terminalStates()).contains(State.STATE1);

		builder.terminateIn(State.STATE1, State.STATE2);

		assertThat(builder.terminalStates()).hasSize(2);
		assertThat(builder.terminalStates()).contains(State.STATE1, State.STATE2);

		builder.terminateIn();

		failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
	}

	@Test public void testIfFailed() {
		final Consumer<Throwable> c = t -> {};

		assertThat(builder.exceptionHandler()).isNotNull();

		builder.whenFailedCall(c);

		assertThat(builder.exceptionHandler()).isEqualTo(c);
	}

	@Test public void testMinimalBuildDoesNotThrow() {
		assertThatCode(() -> builder.withName(SERVICE_NAME)
		                            .startWith(State.STATE1)
		                            .terminateIn(State.STATE3)
		                            .notifyOn(new EventBus())
		                            .whenFailedCall(t -> {})
		                            .in(State.STATE1)
		                            .on(Event.EVENT1)
		                            .goTo(State.STATE2)
		                            .commit()
		                            .build()).doesNotThrowAnyException();
	}

}
