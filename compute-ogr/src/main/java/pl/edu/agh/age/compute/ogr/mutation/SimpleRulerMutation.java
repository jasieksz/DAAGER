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

package pl.edu.agh.age.compute.ogr.mutation;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ogr.RulerUtils;
import pl.edu.agh.age.compute.ogr.problem.RulerProblem;
import pl.edu.agh.age.compute.ogr.solution.Ruler;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.rand.RandomUtils;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class SimpleRulerMutation implements Mutation<Ruler> {

	private final Random rand = ThreadLocalRandom.current();

	private final RulerProblem problemDefinition;

	private final double mutationProbability;

	public SimpleRulerMutation(final RulerProblem problemDefinition, final double mutationProbability) {
		this.problemDefinition = requireNonNull(problemDefinition);
		this.mutationProbability = mutationProbability;
	}

	@Override public Ruler mutate(final Ruler ruler) {
		return (rand.nextDouble() < mutationProbability) ? performMutation(ruler) : ruler;
	}

	private Ruler performMutation(final Ruler ruler) {
		// 1. Randomly choose how many distances will be modified.

		final int distancesToModify = Math.max(ruler.marksCount() / 3, 1);

		// 2. Generate distances that do not appear in the input ruler.

		final List<Integer> currentDistances = RulerUtils.getMeasurableDistances(ruler, false);
		currentDistances.add(0, 1); // make sure that "1" distance is at the beginning of a list
		final Collection<Integer> newDistances = RandomUtils.generateSequence(1, problemDefinition.maxAllowedDistance(),
		                                                                      ruler.marksCount() - 1, currentDistances);
		currentDistances.remove(0); // temporarily remove "1" distance

		// 3. Exchange randomly selected distances to the new generated distances.

		final List<Integer> newDistancesList = new ArrayList<>(newDistances);
		final Collection<Integer> indexesToChange = RandomUtils.generateSequence(0, currentDistances.size() - 1,
		                                                                         distancesToModify);
		int i = 0;
		for (final int index : indexesToChange) {
			currentDistances.set(index, newDistancesList.get(i++));
		}
		final int randomIndexForOne = rand.nextInt(currentDistances.size());
		currentDistances.add(randomIndexForOne, 1);

		Preconditions.checkState(currentDistances.size() == (ruler.marksCount() - 1));

		return new Ruler(currentDistances, false);
	}

}
