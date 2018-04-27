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
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.Booleans;

import java.util.List;

/**
 * Labs Evaluator class with cache.
 *
 * <p>
 * Think at least twice before deciding to use it. Since LABS problem has 2^L different sequence representations, the
 * cache grows quickly consuming enormous amount of memory. For instance, a single cache entry for LABS of a length 50
 * takes 328 bytes of RAM, meaning that 1 million entries will take more than 300 MB.
 * </p>
 *
 * <p>
 * Also, the bigger the problem size is, the less probable is that the cache will prove to be useful. For instance, when
 * LABS of a length 50 was computed for 300 seconds with 10 million cache capacity set, the total count of successful
 * cache queries was 0!
 * </p>
 *
 * <p>Summing up, treat this class rather like an experiment than something useful at all...</p>
 */
public final class LabsEvaluatorWithCache extends LabsEvaluator {

	private final Cache<List<Boolean>, Double> evaluationMap;

	public LabsEvaluatorWithCache(final EvaluatorCounter counter, final long cacheCapacity) {
		super(counter);
		evaluationMap = CacheBuilder.newBuilder().maximumSize(cacheCapacity).build();
	}

	@Override public double evaluate(final LabsSolution sequence) {
		final List<Boolean> representation = Booleans.asList(sequence.sequenceRepresentation());
		Double evaluation = evaluationMap.getIfPresent(representation);
		if (evaluation == null) {
			evaluation = super.evaluate(sequence);
			evaluationMap.put(representation, evaluation);
		}
		return evaluation;
	}

}
