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

package pl.edu.agh.age.compute.labs.improvement;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;

import com.google.common.primitives.Booleans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LabsSelfAvoidingWalkSearch extends AbstractLabsSawSearch {

	private final Set<Double> evalsWalkList = new HashSet<>();

	private final Set<List<Boolean>> solutionsWalkList = new HashSet<>();

	public LabsSelfAvoidingWalkSearch(final LabsEvaluator evaluator, final int iterations,
	                                  final boolean useFastFlipAlgorithm) {
		super(evaluator, iterations, useFastFlipAlgorithm);
	}

	@Override protected void clearWalkList() {
		evalsWalkList.clear();
		solutionsWalkList.clear();
	}

	@Override protected void addToWalkList(final LabsSolution solution) {
		evalsWalkList.add(solution.fitnessValue());
		solutionsWalkList.add(Booleans.asList(solution.sequenceRepresentation()));
	}

	@Override protected boolean isInWalkList(final LabsSolution solution) {
		final double evaluation = solution.fitnessValue();
		if (evalsWalkList.contains(evaluation)) {
			final List<Boolean> solutionList = Booleans.asList(solution.sequenceRepresentation());
			return solutionsWalkList.contains(solutionList);
		}
		return false;
	}

}
