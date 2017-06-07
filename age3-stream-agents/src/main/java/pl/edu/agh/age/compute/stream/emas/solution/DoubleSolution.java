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

package pl.edu.agh.age.compute.stream.emas.solution;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Solution optimized for a single double number.
 */
public final class DoubleSolution implements Solution<Double> {

	private static final long serialVersionUID = -1330253069016370993L;

	private final double value;

	private final double fitness;

	public DoubleSolution(final double value, final double fitness) {
		this.value = value;
		this.fitness = fitness;
	}

	public DoubleSolution(final double value) {
		this(value, Double.NaN);
	}

	public double value() {
		return value;
	}

	@Override public double fitnessValue() {
		return fitness;
	}

	@Override public DoubleSolution withFitness(final double newFitness) {
		return new DoubleSolution(value, newFitness);
	}

	@Override public Double unwrap() {
		return value;
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(value).toString();
	}
}
