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

package pl.edu.agh.age.compute.stream.emas.solution;

import java.io.Serializable;

/**
 * Utilities and factory methods for solutions.
 */
public final class Solutions {
	private Solutions() {}

	public static <T extends Serializable> Solution<T> simple(final T value) {
		return new SimpleSolution<>(value);
	}

	public static DoubleSolution singleDouble(final double value) {
		return new DoubleSolution(value);
	}
}
