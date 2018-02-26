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

package pl.edu.agh.age.util.fsm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import com.google.common.eventbus.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.function.Consumer;

public final class DefaultStateMachineServiceTest {
	private enum State {
		STATE1,
		STATE2,
		STATE3,
		END
	}

	private enum Event {
		EVENT1,
		EVENT2,
		EVENT3,
		GLOBAL_EVENT,
	}

	private static final String SERVICE_NAME = "name";

	private static final Consumer<FSM<State, Event>> consumer1 = fsm -> {};

	private static final Consumer<FSM<State, Event>> consumer2 = fsm -> {};

	private final List<Throwable> collectedThrowables = newArrayList();

	private final Consumer<Throwable> exceptionHandler = collectedThrowables::add;

	@Mock private EventBus eventBus;

	private StateMachineServiceBuilder<State, Event> builder;

	private DefaultStateMachineService<State, Event> fsmService;

	@SuppressWarnings("ProhibitedExceptionThrown") @Before public void setUp() {
		MockitoAnnotations.initMocks(this);

		collectedThrowables.clear();
		builder = StateMachineServiceBuilder.withStatesAndEvents(State.class, Event.class);
		//@formatter:off
		builder.withName(SERVICE_NAME)
		       .startWith(State.STATE1)
		       .notifyOn(eventBus)
		       .terminateIn(State.END)
		       .whenFailedCall(exceptionHandler)
		       .in(State.STATE1)
			       .on(Event.EVENT1)
			       .goTo(State.STATE2)
			       .commit()
		       .in(State.STATE2)
			       .on(Event.EVENT2)
			       .execute(fsm -> {})
			       .goTo(State.STATE3)
			       .commit()
		       .in(State.STATE1)
			       .on(Event.EVENT2)
			       .goTo(State.STATE2)
			       .execute(fsm -> {
				       throw new RuntimeException("FAILED");
			       })
			       .commit()
		       .inAnyState()
				   .on(Event.GLOBAL_EVENT)
	               .goTo(State.END)
	               .commit()
		       .synchronous();
		//@formatter:on
		fsmService = (DefaultStateMachineService<State, Event>)builder.build();
	}

	@Test public void testInitialState() {
		//assertThat(fsmService.isRunning()).isTrue();
		assertThat(fsmService.isInState(State.STATE1)).isTrue();
	}

	@Test public void testSingleTransition_correct() {
		fsmService.fire(Event.EVENT1);
		fsmService.execute();

		assertThat(fsmService.isInState(State.STATE2)).isTrue();
	}

	@Test public void testSingleTransition_unexpectedEvent() {
		fsmService.fire(Event.EVENT3);
		fsmService.execute();

		assertThat(fsmService.isFailed()).isFalse();
		assertThat(fsmService.isTerminated()).isFalse();
		assertThat(fsmService.isInState(State.STATE1)).isTrue();
	}

	@SuppressWarnings("NewExceptionWithoutArguments") @Test public void testFailure() {
		final Exception e = new Exception();
		fsmService.failWithError(e);
		fsmService.execute();

		assertThat(fsmService.isFailed()).isTrue();
	}

	@Test public void testSingleTransition_globalEvent() {
		fsmService.fire(Event.GLOBAL_EVENT);
		fsmService.execute();

		assertThat(fsmService.isFailed()).isFalse();
		assertThat(fsmService.isInState(State.END)).isTrue();
	}

	@Test public void testSingleTransition_failedAction() {
		fsmService.fire(Event.EVENT2);
		fsmService.execute();

		assertThat(fsmService.isInState(State.STATE2)).isFalse();

		assertThat(collectedThrowables).isNotNull().hasSize(1);
		assertThat(collectedThrowables.get(0)).isInstanceOf(RuntimeException.class);
	}

	@Test public void testMultipleTransitions() {
		fsmService.fire(Event.EVENT1);
		fsmService.execute();
		fsmService.fire(Event.EVENT2);
		fsmService.execute();

		assertThat(fsmService.isInState(State.STATE3)).isTrue();

		assertThat(collectedThrowables).isNotNull().hasSize(0);
	}
}
