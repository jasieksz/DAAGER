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

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A descriptor of the transition in the transition table.
 * <p>
 * Package-protected class.
 *
 * @param <S>
 * 		a states type.
 * @param <E>
 * 		an events type
 */
@SuppressWarnings("unchecked")
final class TransitionDescriptor<S extends Enum<S>, E extends Enum<E>> {

	private static final Consumer<FSM<Dummy, Dummy>> ILLEGAL_ACTION = fsm -> {
		throw new IllegalTransitionException("Transition is illegal.");
	};

	/**
	 * Placeholder for null descriptor.
	 */
	private enum Dummy {
		NULL
	}

	private static final TransitionDescriptor<Dummy, Dummy> NULL = new TransitionDescriptor<Dummy, Dummy>(Dummy.NULL, Dummy.NULL,
	                                                                               Collections.<Dummy>emptySet(),
	                                                                                                     (Consumer<FSM<Dummy, Dummy>>)ILLEGAL_ACTION);

	private static final Consumer<?> EMPTY_ACTION = fsm -> {};

	private final S initial;

	private final E event;

	private final Consumer<FSM<S, E>> action;

	private final Set<S> target;

	TransitionDescriptor(final S initial, final E event, final Collection<S> target,
	                     final @Nullable Consumer<FSM<S, E>> action) {
		this.initial = initial;
		this.event = event;
		this.action = (action == null) ? (Consumer<FSM<S, E>>)EMPTY_ACTION : action;
		this.target = ImmutableSet.copyOf(requireNonNull(target));
	}

	static <V extends Enum<V>, Z extends Enum<Z>> TransitionDescriptor<V, Z> nullDescriptor() {
		return (TransitionDescriptor<V, Z>)NULL;
	}

	Consumer<FSM<S, E>> action() {
		return action;
	}

	Set<S> target() {
		return target;
	}

	S initial() {
		return initial;
	}

	E event() {
		return event;
	}

	@Override public String toString() {
		return format("(%s : %s : %s)", initial, event, target);
	}
}
