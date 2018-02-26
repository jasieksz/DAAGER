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

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

/**
 * A state within a state machine.
 *
 * @param <S>
 * 		type of states in the state machine
 * @param <E>
 * 		type of events in the state machine
 */
final class State<S extends Enum<S>, E extends Enum<E>> {

	private final S name;

	private final boolean terminal;

	private final Map<E, Transition<S, E>> transitions;

	private final Seq<S> targetStates;

	State(final S name, final boolean terminal, final Map<E, Transition<S, E>> transitions) {
		this.name = requireNonNull(name);
		this.terminal = terminal;
		this.transitions = requireNonNull(transitions);
		targetStates = transitions.values().flatMap(Transition::targets);
	}

	public S name() {
		return name;
	}

	public Option<Transition<S, E>> transitionForEvent(final E event) {
		return transitions.get(event);
	}

	public Seq<Transition<S, E>> transitions() {
		return transitions.values();
	}

	public boolean isTerminal() {
		return terminal;
	}

	public boolean is(final S other) {
		return Objects.equals(name, other);
	}

	public boolean isNextState(final S other) {
		return targetStates.contains(other);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
		                  .add("name", name)
		                  .add("terminal", terminal)
		                  .toString();
	}

}
