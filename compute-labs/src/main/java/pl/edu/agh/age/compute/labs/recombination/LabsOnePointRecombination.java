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

package pl.edu.agh.age.compute.labs.recombination;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class LabsOnePointRecombination implements Recombination<LabsSolution> {

	@Override public LabsSolution recombine(final LabsSolution firstSolution, final LabsSolution secondSolution) {

		// agent1        xxxxxx
		// agent2:       oooooo
		// result agent: xxoooo

		checkArgument(firstSolution.length() == secondSolution.length());
		final Random rand = ThreadLocalRandom.current();
		final int length = firstSolution.length();
		final int index = 1 + rand.nextInt(length - 2);

		final boolean[] resultSolution = new boolean[length];
		for (int i = 0; i < length; i++) {
			if (i < index) {
				resultSolution[i] = firstSolution.sequenceRepresentation()[i];
			} else {
				resultSolution[i] = secondSolution.sequenceRepresentation()[i];
			}
		}
		return new LabsSolution(resultSolution);
	}

}
