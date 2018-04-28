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


package pl.edu.agh.age.compute.ea.variation.recombination.real;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.rand.DoubleRandomGenerator;

/**
 * Recombination strategy that averages randomly between two individuals.
 */
public final class RandomRecombine extends DoubleContinuousRecombine {

	private final DoubleRandomGenerator rand;

	public RandomRecombine(final DoubleRandomGenerator rand) {
		this.rand = requireNonNull(rand);
	}

	@Override protected double recombine(final double a, final double b) {
		return Math.min(a, b) + rand.nextDouble() * (Math.max(a, b) - Math.min(a, b));
	}
}
