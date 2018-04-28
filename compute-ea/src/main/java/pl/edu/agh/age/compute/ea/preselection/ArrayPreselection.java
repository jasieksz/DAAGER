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

import pl.edu.agh.age.compute.ea.solution.Solution;

import java.io.Serializable;

import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;

/**
 * Abstract {@link Preselection} implementation. Relies on subclasses to provide the indices of the preselected solution
 * (note that any solution may be chosen multiple times).
 */
public abstract class ArrayPreselection<R extends Serializable, T extends Solution<Array<R>>>
	implements Preselection<T> {

	@Override public final List<T> preselect(final List<T> population) {
		requireNonNull(population, "The population must not be null");

		final int populationSize = population.size();
		if (populationSize < 2) {
			return population;
		}

		final double[] evaluations = population.toJavaStream().mapToDouble(Solution::evaluationValue).toArray();
		final int[] indices = preselectIndices(evaluations);

		HashSet<Integer> selectedIndices = HashSet.empty();
		List<T> preselectedSolutions = List.empty();
		for (final int index : indices) {
			final T solution = population.get(index);
			if (selectedIndices.contains(index)) {
				preselectedSolutions = preselectedSolutions.append((T)solution.cloneWithNewValue(solution.unwrap()));
			} else {
				preselectedSolutions = preselectedSolutions.append(solution);
			}
			selectedIndices = selectedIndices.add(index);
		}
		return preselectedSolutions;
	}

	/**
	 * Performs the actual preselection. Given an array of evaluation values, returns an array containing indices of the
	 * preselected solutions. Any given solution may be preselected multiple times, in which case it will be copied in
	 * the resulting preselected population.
	 *
	 * @param values
	 * 		The solutions evaluation values
	 *
	 * @return The indices of the preselected solutions
	 */
	protected abstract int[] preselectIndices(double[] values);
}

