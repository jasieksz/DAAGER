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

import pl.edu.agh.age.compute.ea.rand.IntRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.Solution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import io.vavr.collection.Array;

/**
 * Tournament preselect implementation.
 */
public final class TournamentPreselection<R extends Serializable, T extends Solution<Array<R>>>
	extends ArrayPreselection<R, T> {

	private static final Logger logger = LoggerFactory.getLogger(TournamentPreselection.class);

	private final IntRandomGenerator rand;

	public TournamentPreselection(final IntRandomGenerator rand) {
		this.rand = requireNonNull(rand);
	}

	@Override protected int[] preselectIndices(final double[] values) {
		final int n = values.length;
		final int[] indices = new int[n];

		for (int i = 0; i < n; i++) {
			final int first = rand.nextInt(n);
			final int second = rand.nextInt(n);
			indices[i] = values[first] > values[second] ? first : second;
		}

		logger.debug("Tournament resulted in indices {}", indices);

		return indices;
	}
}
