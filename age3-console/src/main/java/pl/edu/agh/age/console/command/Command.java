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
 * Interface for commands used by the pl.edu.agh.age.console.
 */
public interface Command {

	/**
	 * Name of the command (for JavaScript side)
	 */
	String name();

	/**
	 * Checks if the object is of given klass, and if not, throws an exception
	 *
	 * This version requires `obj` to be nonnull.
	 *
	 * @param obj
	 * 		an object to check
	 * @param klass
	 * 		expected class
	 * @param msg
	 * 		message for the exception
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `obj` cast to `klass`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` is not instance of `klass`. `msg` is used as a message string
	 */
	static <T> T checkAndCast(final Object obj, final Class<T> klass, final String msg) {
		if (klass.isInstance(obj)) {
			return klass.cast(obj);
		}
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Returns the named object from the map only if the object is of given klass, and if not, throws an exception
	 *
	 * This version requires `name` to point to a nonnull object.
	 *
	 * @param parameters
	 * 		name -> object mapping
	 * @param name
	 * 		name of the parameter to get
	 * @param klass
	 * 		expected class
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `name` object from `parameters` map cast to `klass`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` the object is not instance of `klass`. `msg` is used as a message string
	 * @see #checkAndCast(Object, Class, String)
	 */
	static <T> T getAndCast(final Map<String, Object> parameters, final String name, final Class<T> klass) {
		final Object obj = parameters.get(name);
		return checkAndCast(obj, klass,
		                    format("%s is required to be %s, %s provided instead", name, klass, obj.getClass()));
	}

	/**
	 * Checks if the object is of given klass, and if not, throws an exception
	 *
	 * This version accepts `null` and returns empty {@link Optional} in such a case.
	 *
	 * @param obj
	 * 		an object to check
	 * @param klass
	 * 		expected class
	 * @param msg
	 * 		message for the exception
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `obj` cast to `klass`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` is not instance of `klass`. `msg` is used as a message string
	 */
	static <T> Optional<T> checkAndCastNullable(final @Nullable Object obj, final Class<T> klass, final String msg) {
		if (obj == null) {
			return Optional.empty();
		}
		if (klass.isInstance(obj)) {
			return Optional.of(klass.cast(obj));
		}
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Returns the named object from the map only if the object is of given klass, and if not, throws an exception
	 *
	 * This version accepts `name` pointing to a null object.
	 *
	 * @param parameters
	 * 		name -> object mapping
	 * @param name
	 * 		name of the parameter to get
	 * @param klass
	 * 		expected class
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `name` object from `parameters` map cast to `klass`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` the object is not instance of `klass`. `msg` is used as a message string
	 * @see #checkAndCastNullable(Object, Class, String)
	 */
	static <T> Optional<T> getAndCastNullable(final Map<String, Object> parameters, final String name,
	                                          final Class<T> klass) {
		final Object obj = parameters.get(name);
		return checkAndCastNullable(obj, klass, format("%s is required to be %s, %s provided instead", name, klass,
		                                               obj.getClass()));
	}

	/**
	 * Checks if the object is of given klass, and if not, throws an exception
	 *
	 * This version accepts `null` and returns the provided default value.
	 *
	 * @param obj
	 * 		an object to check
	 * @param klass
	 * 		expected class
	 * @param def
	 * 		default value to return
	 * @param msg
	 * 		message for the exception
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `obj` cast to `klass` or `def`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` is not instance of `klass`. `msg` is used as a message string
	 */
	static <T> T checkAndCastDefault(final @Nullable Object obj, final Class<T> klass, final T def, final String msg) {
		if (obj == null) {
			return def;
		}
		if (klass.isInstance(obj)) {
			return klass.cast(obj);
		}
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Returns the named object from the map only if the object is of given klass, and if not, throws an exception
	 *
	 * This version accepts `name` pointing to a null object.
	 *
	 * @param parameters
	 * 		name -> object mapping
	 * @param name
	 * 		name of the parameter to get
	 * @param klass
	 * 		expected class
	 * @param def
	 * 		default value to return
	 * @param <T>
	 * 		expected type of the object
	 *
	 * @return the `name` object from `parameters` map cast to `klass`, if not possible - throws an exception
	 *
	 * @throws IllegalArgumentException
	 * 		when `obj` the object is not instance of `klass`. `msg` is used as a message string
	 * @see #checkAndCastDefault(Object, Class, Object, String)
	 */
	static <T> T getAndCastDefault(final Map<String, Object> parameters, final String name, final Class<T> klass,
	                               final T def) {
		final Object obj = parameters.get(name);
		return checkAndCastDefault(obj, klass, def,
		                           format("%s is required to be %s, %s provided instead", name, klass, obj.getClass()));
	}

}
