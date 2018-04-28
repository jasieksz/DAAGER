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


package pl.edu.agh.age.compute.ea.preselection;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.distribution.CumulativeDistribution;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.scaling.Scaling;
import pl.edu.agh.age.compute.ea.solution.Solution;

import java.io.Serializable;

import io.vavr.collection.Array;

/**
 * Roulette wheel implementation. Scales solutions evaluations using a provided {@link Scaling}.
 */
public final class RoulettePreselection<R extends Serializable, T extends Solution<Array<R>>>
	extends ArrayPreselection<R, T> {

	private final Scaling scaling;

	private final NormalizedDoubleRandomGenerator rand;

	public RoulettePreselection(final Scaling scaling, final NormalizedDoubleRandomGenerator rand) {
		this.scaling = requireNonNull(scaling);
		this.rand = requireNonNull(rand);
	}

	@Override protected int[] preselectIndices(final double[] values) {
		final CumulativeDistribution distribution = new CumulativeDistribution(scaling.scale(values));

		final int[] indices = new int[values.length];
		for (int i = 0; i < indices.length; i++) {
			indices[i] = distribution.getValueFor(rand.nextDouble());
		}
		return indices;
	}
}
