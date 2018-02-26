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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

import io.vavr.collection.Set;

/**
 * A descriptor of the transition in the transition table.
 *
 * @param <S>
 * 		a states type
 * @param <E>
 * 		an events type
 */
final class Transition<S extends Enum<S>, E extends Enum<E>> {

	private final S initial;

	private final E event;

	private final Set<S> targets;

	private final Consumer<FSM<S, E>> action;

	Transition(final S initial, final E event, final Set<S> targets, final @Nullable Consumer<FSM<S, E>> action) {
		this.initial = requireNonNull(initial);
		this.event = requireNonNull(event);
		this.targets = requireNonNull(targets);
		this.action = (action == null) ? x -> {} : action;
	}

	S initial() {
		return initial;
	}

	E event() {
		return event;
	}

	Set<S> targets() {
		return targets;
	}

	Consumer<FSM<S, E>> action() {
		return action;
	}

	@Override public String toString() {
		return format("(%s : %s : %s)", initial, event, targets);
	}
}
