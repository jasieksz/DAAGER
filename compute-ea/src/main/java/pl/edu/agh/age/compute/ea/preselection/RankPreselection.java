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

import io.vavr.collection.List;

/**
 * Rank preselection, acting as a decorator on some other preselection.
 *
 * Ranks the given population, then update each solution's evaluation to be inversely proportional to that solution's
 * rank, i.e. the worst will have fitness 1 and the best will have fitness N.
 */
public final class RankPreselection<R extends Serializable, T extends Solution<R>> implements Preselection<T> {

	private static final long serialVersionUID = -1905429483671559398L;

	private final Preselection<T> preselection;

	public RankPreselection(final Preselection<T> preselection) {
		this.preselection = requireNonNull(preselection);
	}

	@Override public List<T> preselect(final List<T> population) {
		requireNonNull(population, "The population must not be null");

		final List<T> solutionsByEvaluation = population.sortBy(Solution::evaluationValue);
		final List<? super T> rankedPopulation = solutionsByEvaluation.zipWithIndex()
		                                                              .map(t -> t._1.withEvaluation(t._2));

		return preselection.preselect((List<T>)rankedPopulation);
	}
}
