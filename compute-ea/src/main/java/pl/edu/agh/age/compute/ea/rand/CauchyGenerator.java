/*
 * Copyright (C) 2006-2018 Intelligent Information Systems Group.
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

import static java.util.Objects.requireNonNull;

/**
 * An implementation of {@link DoubleSymmetricGenerator} which generates values on the base of a Cauchy distribution.
 */
public final class CauchyGenerator implements DoubleSymmetricGenerator {

	private static final double DEFAULT_LOCATION = 0.0;

	private static final double DEFAULT_SCALE = 1.0;

	private final NormalizedDoubleRandomGenerator rand;

	public CauchyGenerator(final NormalizedDoubleRandomGenerator rand) {
		this.rand = requireNonNull(rand);
	}

	@Override public double nextDouble() {
		return nextDouble(DEFAULT_LOCATION, DEFAULT_SCALE);
	}

	@Override public double nextDouble(final double location, final double scale) {
		final double seed = rand.nextDouble();
		return location + scale * Math.tan(Math.PI * (seed - 0.5));
	}

	@Override public double lowerDouble() {
		return Double.MIN_VALUE;
	}

	@Override public double upperDouble() {
		return Double.MAX_VALUE;
	}
}
