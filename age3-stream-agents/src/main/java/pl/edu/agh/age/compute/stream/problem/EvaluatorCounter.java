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

package pl.edu.agh.age.compute.stream.problem;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The interface responsible for counting fitness evaluation calls.
 */
public interface EvaluatorCounter {

	/**
	 * Increments the counter. Method implementation should be thread-safe.
	 */
	void increment();

	/**
	 * Gets the counter value. Method implementation should be thread-safe.
	 *
	 * @return the fitness evaluator count
	 */
	long get();

	static EvaluatorCounter empty() {
		return new EvaluatorCounter() {
			@Override public void increment() {}

			@Override public long get() {
				return 0;
			}
		};
	}

	static EvaluatorCounter simpleCounter() {
		return new EvaluatorCounter() {
			private final AtomicLong counter = new AtomicLong(0);

			@Override public void increment() {
				counter.incrementAndGet();
			}

			@Override public long get() {
				return counter.get();
			}
		};
	}

}
