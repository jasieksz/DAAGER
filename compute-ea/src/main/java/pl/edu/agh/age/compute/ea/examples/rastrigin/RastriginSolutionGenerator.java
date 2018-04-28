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

package pl.edu.agh.age.compute.ea.examples.rastrigin;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.problem.real.RastriginProblem;
import pl.edu.agh.age.compute.ea.rand.NormalizedDoubleRandomGenerator;
import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;
import pl.edu.agh.age.compute.ea.solution.Solutions;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

import io.vavr.Function0;

public final class RastriginSolutionGenerator implements Function0<DoubleVectorSolution> {

	private final RastriginProblem problem;

	private final NormalizedDoubleRandomGenerator randomGenerator;

	@Inject
	public RastriginSolutionGenerator(final @Value("${problem.dimensions}") int dimensions,
	                                  final NormalizedDoubleRandomGenerator randomGenerator) {
		checkArgument(dimensions > 0);
		problem = new RastriginProblem(dimensions);
		this.randomGenerator = requireNonNull(randomGenerator);
	}

	@Override public DoubleVectorSolution apply() {
		return Solutions.randomDoubleVectorForProblem(problem, randomGenerator);
	}
}
