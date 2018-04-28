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

package pl.edu.agh.age.compute.ea;


import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public final class BestSolutionAfterStepAction implements AfterStepAction<Solution<?>, StatisticsKeys> {

	private static final long serialVersionUID = -5852903273256254808L;

	@Override
	public Map<StatisticsKeys, Object> apply(final Long workplaceId, final Long step,
	                                         final List<Solution<?>> population) {
		final Solution<?> bestSolution = computeBestSolution(population).getOrElseThrow(
			() -> new AssertionError("Best solution should not be empty"));
		return HashMap.of(StatisticsKeys.STEP_NUMBER, step, StatisticsKeys.CURRENT_BEST, bestSolution);
	}


	// FIXME: Utility class?
	private static Option<Solution<?>> computeBestSolution(final List<Solution<?>> population) {
		return population.maxBy(Solution::evaluationValue);
	}

}
