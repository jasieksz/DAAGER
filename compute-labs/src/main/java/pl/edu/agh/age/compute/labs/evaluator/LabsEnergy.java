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

package pl.edu.agh.age.compute.labs.evaluator;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;

public final class LabsEnergy {

	private final int energy;

	public LabsEnergy(final LabsSolution sequence) {
		int energy = 0;
		for (int k = 1; k < sequence.length(); k++) {
			final int autocorrelation = calculateAutocorrelation(sequence.sequenceRepresentation(), k);
			energy += autocorrelation * autocorrelation;
		}
		this.energy = energy;
	}


	private static int calculateAutocorrelation(final boolean[] sequenceRepresentation, final int distance) {
		// sequence aperiodic autocorrelation
		final int length = sequenceRepresentation.length;
		int autocorrelation = 0;
		for (int i = 0; i < length - distance; i++) {
			final int s_i = sequenceRepresentation[i] ? 1 : -1;
			final int s_ik = sequenceRepresentation[i + distance] ? 1 : -1;
			autocorrelation += s_i * s_ik;
		}
		return autocorrelation;
	}

	public int value() {
		return energy;
	}

}
