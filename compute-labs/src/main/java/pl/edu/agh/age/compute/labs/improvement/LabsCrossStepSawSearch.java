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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.Booleans;

import java.util.List;

/**
 * This is a variant of SelfAvoidingWalk improvement operator for LABS problem, which uses a single walkList shared with
 * all agents in the population. Each iteration adds an entry to the list, which can influence improvement computation
 * for future agents, by eliminating repetitions of already processed solutions.
 *
 * The implementation uses a limited size cache mechanism with entry eviction. Thus, it has a high memory usage. For
 * LABS of length 201, a single cache entry takes around 1000 bytes of memory. If you intent to use this improvement
 * operator in AgE computation, make sure that cacheSize is set to the value that will not cause exceeding current Java
 * maximum heapsize value.
 */
public final class LabsCrossStepSawSearch extends AbstractLabsSawSearch {

	private final Cache<List<Boolean>, Double> walkList;

	public LabsCrossStepSawSearch(final LabsEvaluator evaluator, final int iterations, final long cacheSize,
	                              final boolean useFastFlipAlgorithm) {
		super(evaluator, iterations, useFastFlipAlgorithm);
		walkList = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
	}

	@Override protected void clearWalkList() {
		// Cache is evicted automatically
	}

	@Override protected void addToWalkList(final LabsSolution solution) {
		final List<Boolean> solutionList = Booleans.asList(solution.sequenceRepresentation());
		walkList.put(solutionList, solution.fitnessValue());
	}

	@Override protected boolean isInWalkList(final LabsSolution solution) {
		final List<Boolean> solutionList = Booleans.asList(solution.sequenceRepresentation());
		return walkList.getIfPresent(solutionList) != null;
	}

}
