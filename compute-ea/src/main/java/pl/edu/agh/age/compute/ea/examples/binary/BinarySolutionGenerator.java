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

package pl.edu.agh.age.compute.ea.examples.binary;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.problem.binary.M7Problem;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;
import pl.edu.agh.age.compute.ea.solution.Solutions;

import javax.inject.Inject;

import io.vavr.Function0;

public final class BinarySolutionGenerator implements Function0<BooleanVectorSolution> {

	private final M7Problem problem = new M7Problem();

	private final NormalizedDoubleRandomGenerator randomGenerator;

	@Inject public BinarySolutionGenerator(final NormalizedDoubleRandomGenerator randomGenerator) {
		this.randomGenerator = requireNonNull(randomGenerator);
	}

	@Override public BooleanVectorSolution apply() {
		return Solutions.randomBooleanVectorForProblem(problem, randomGenerator);
	}
}
