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

package pl.edu.agh.age.compute.api;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * Simple query provider for compute-level.
 *
 * The query processor operates on cached, not up-to-date data. It is the responsibility of the compute application
 * to prepare the data for cache (through providing a {@link Callable} to the {@link #schedule(Callable)} method.
 * Each instance (separate node) of the compute application can prepare only one object at time (each consecutive
 * returned objects will replace the old one.
 *
 * @param <T>
 * 		type of objects that are queried. It is up to the compute application to know what it will be using.
 */
public interface QueryProcessor<T> {

	/**
	 * Create a stream of objects to query.
	 *
	 * @return a stream.
	 */
	Stream<T> query();

	/**
	 * Schedule a cache generator.
	 *
	 * @param callable
	 * 		callable that creates objects for future queries.
	 */
	void schedule(Callable<T> callable);

}
