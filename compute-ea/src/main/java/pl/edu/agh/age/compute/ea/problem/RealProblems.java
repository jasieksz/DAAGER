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

package pl.edu.agh.age.compute.ea.problem;

import pl.edu.agh.age.compute.ea.problem.real.AckleyProblem;
import pl.edu.agh.age.compute.ea.problem.real.GriewankProblem;
import pl.edu.agh.age.compute.ea.problem.real.RastriginProblem;
import pl.edu.agh.age.compute.ea.problem.real.RosenbrockProblem;
import pl.edu.agh.age.compute.ea.problem.real.SchwefelProblem;
import pl.edu.agh.age.compute.ea.problem.real.SphereProblem;

public final class RealProblems {
	private RealProblems() {}

	public static ParallelProblem<Double> ackleyProblem(final int dimension) {
		return new AckleyProblem(dimension);
	}

	public static ParallelProblem<Double> griewankProblem(final int dimension) {
		return new GriewankProblem(dimension);
	}

	public static ParallelProblem<Double> rastriginProblem(final int dimension) {
		return new RastriginProblem(dimension);
	}

	public static ParallelProblem<Double> rosenbrockProblem(final int dimension) {
		return new RosenbrockProblem(dimension);
	}

	public static ParallelProblem<Double> schwefelProblem(final int dimension) {
		return new SchwefelProblem(dimension);
	}

	public static ParallelProblem<Double> sphereProblem(final int dimension) {
		return new SphereProblem(dimension);
	}
}
