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


package pl.edu.agh.age.compute.ea.variation.mutation.real;

import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;

/**
 * Simple population mutation strategy, that mutates each solution individually using provided solution mutation
 * strategy.
 */
public final class NormalMicroMacroMutate extends DoubleStochasticMutate {

	private final NormalizedDoubleRandomGenerator rand;

	private final double mutationRange;

	public NormalMicroMacroMutate(final NormalizedDoubleRandomGenerator rand, final IntRandomGenerator intRand,
	                              final double mutationRange) {
		super(rand, intRand);
		this.rand = rand;
		this.mutationRange = mutationRange;
	}

	@Override protected double doMutate(final double value) {
		// ,,Cauchy'' distribution...
		// stdev = mutationRange
		double range = mutationRange;
		final double p = rand.nextDouble();
		if (p < 0.2) {
			range = mutationRange * 5.0;
		} else if (p < 0.4) {
			range = mutationRange / 5.0;
		}

		// Based on Wikipedia's information on inverse cumulative
		// distribution function of the Cauchy distribution
		return value + range * Math.tan(Math.PI * (rand.nextDouble() - 0.5));
	}

}
