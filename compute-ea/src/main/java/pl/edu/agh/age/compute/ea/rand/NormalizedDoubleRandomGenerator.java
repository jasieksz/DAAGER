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

/**
 * This interface introduces additional semantics to the {@link DoubleRandomGenerator} one.
 *
 * It is designed for generators which value is contained in the range [0, 1), i.e. {@code lowerDouble()} should
 * return 0 and {@code upperDouble()} should return 1, and {@code nextDouble()} is guaranteed to be in the
 * range [0, 1).
 */
public interface NormalizedDoubleRandomGenerator extends DoubleRandomGenerator {

	/**
	 * Returns a random double value arbitrarily distributed in the range [0, 1).
	 *
	 * @return A random value.
	 */
	@Override double nextDouble();

	/**
	 * @return 0
	 */
	@Override double lowerDouble();

	/**
	 * @return 1
	 */
	@Override double upperDouble();
}
