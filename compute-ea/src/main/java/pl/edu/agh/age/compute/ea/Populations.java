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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.collection.List;

public final class Populations {

	private static final Logger logger = LoggerFactory.getLogger(Populations.class);

	private Populations() {}

	public static <T extends Serializable, S extends Solution<T>> List<S> createPopulation(final int size,
	                                                                                       final Function0<S> solutionGenerator) {
		List<S> solutions = List.empty();
		for (int i = 0; i < size; i++) {
			solutions = solutions.append(solutionGenerator.apply());
		}
		logger.debug("Population of {} solutions was generated", size);
		return solutions;
	}

	public static <T extends Serializable, S extends Solution<T>> List<S> createPopulation(final int size,
	                                                                                       final Function1<Integer, S> solutionGenerator) {
		List<S> solutions = List.empty();
		for (int i = 0; i < size; i++) {
			solutions = solutions.append(solutionGenerator.apply(i));
		}
		logger.debug("Population of {} solutions was generated", size);
		return solutions;
	}
}
