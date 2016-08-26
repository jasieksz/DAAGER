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

package pl.edu.agh.age.console.command;

import static java.lang.String.format;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * A class to simplify creation of commands with suboperations.
 */
public abstract class BaseCommand implements Command {

	protected static <T> T checkAndCast(final Object obj, final Class<T> klass, final String msg) {
		if (klass.isInstance(obj)){
			return klass.cast(obj);
		}
		throw new IllegalArgumentException(msg);
	}

	protected static <T> T getAndCast(final Map<String, Object> parameters, final String name, final Class<T> klass) {
		final Object obj  = parameters.get(name);
		return checkAndCast(obj, klass, format("%s is required to be %s, %s provided instead", name, klass, obj.getClass()));
	}

	protected static <T> Optional<T> checkAndCastNullable(final @Nullable Object obj, final Class<T> klass, final String msg) {
		if (obj == null) {
			return Optional.empty();
		}
		if (klass.isInstance(obj)){
			return Optional.of(klass.cast(obj));
		}
		throw new IllegalArgumentException(msg);
	}

	protected static <T> Optional<T> getAndCastNullable(final Map<String, Object> parameters, final String name, final Class<T> klass) {
		final Object obj  = parameters.get(name);
		return checkAndCastNullable(obj, klass, format("%s is required to be %s, %s provided instead", name, klass, obj.getClass()));
	}

	protected static <T> T checkAndCastDefault(final @Nullable Object obj, final Class<T> klass, final T def, final String msg) {
		if (obj == null) {
			return def;
		}
		if (klass.isInstance(obj)){
			return klass.cast(obj);
		}
		throw new IllegalArgumentException(msg);
	}

	protected static <T> T getAndCastDefault(final Map<String, Object> parameters, final String name, final Class<T> klass, final T def) {
		final Object obj  = parameters.get(name);
		return checkAndCastDefault(obj, klass, def, format("%s is required to be %s, %s provided instead", name, klass, obj.getClass()));
	}
}
