/*
 * Copyright (C) 2016-2016 Intelligent Information Systems Group.
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

import java.util.Random;

public final class JavaRandomGenerator implements NormalizedDoubleRandomGenerator, RandomGenerator {

	private final Random rand;

	public JavaRandomGenerator() {
		this(System.currentTimeMillis());
	}

	public JavaRandomGenerator(final long seed) {
		rand = new Random(seed);
	}

	@Override public int nextInt() {
		return rand.nextInt();
	}

	@Override public int nextInt(final int bound) {
		return rand.nextInt(bound);
	}

	@Override public double nextDouble() {
		return rand.nextDouble();
	}

	@Override public double lowerDouble() {
		return 0;
	}

	@Override public double upperDouble() {
		return 1;
	}

	@Override public int lowerInt() {
		return Integer.MIN_VALUE;
	}

	@Override public int upperInt() {
		return Integer.MAX_VALUE;
	}
}
