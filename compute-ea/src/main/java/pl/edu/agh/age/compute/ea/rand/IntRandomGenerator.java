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

package pl.edu.agh.age.compute.ea.rand;

public interface IntRandomGenerator {

	int nextInt();

	int nextInt(final int bound);

	/**
	 * Specifies the lower bound of the values that can be returned by {@code nextInt()}.
	 *
	 * @return This generator's lower bound.
	 */
	int lowerInt();

	/**
	 * Specifies the upper bound of the values that can be returned by {@code nextInt()}.
	 *
	 * @return This generator's upper bound.
	 */
	int upperInt();
}