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

package pl.edu.agh.age.compute.labs.mutation;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.rand.RandomUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class LabsClassicMutation implements Mutation<LabsSolution> {

	private final double bitsToMutatePercentage;

	public LabsClassicMutation(final double bitsToMutatePercentage) {
		checkArgument((bitsToMutatePercentage > 0) && (bitsToMutatePercentage <= 1),
		              "Mutated genes percentage should be greater than 0.0 and less or equal to 1.0");
		this.bitsToMutatePercentage = bitsToMutatePercentage;
	}

	@Override public LabsSolution mutate(final LabsSolution solution) {
		final Random rand = ThreadLocalRandom.current();
		final int length = solution.length();
		final boolean[] resultSolution = Arrays.copyOf(solution.sequenceRepresentation(), length);

		final int bitsToMutateCount = getBitsToMutateCount(rand, solution);
		final Collection<Integer> bitsToMutate = RandomUtils.generateSequence(0, length - 1, bitsToMutateCount);

		for (final Integer i : bitsToMutate) {
			resultSolution[i] = !resultSolution[i];
		}
		return new LabsSolution(resultSolution);
	}

	private int getBitsToMutateCount(final Random rand, final LabsSolution solution) {
		final int mutatedBitsCount = (int)Math.round(bitsToMutatePercentage * solution.length());
		final double chanceForExtraBit = Math.abs((bitsToMutatePercentage * solution.length()) - mutatedBitsCount);
		if (rand.nextDouble() < chanceForExtraBit) {
			return mutatedBitsCount + 1;
		}
		return mutatedBitsCount;
	}

}
