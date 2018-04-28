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


package pl.edu.agh.age.compute.ea.problem.real;

import pl.edu.agh.age.compute.ea.problem.ParallelProblem;

/**
 * This class represents the problem domain for a floating-point coded Sphere function.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page1113.htm
 * Default range: [-5.12, 5.12]
 */
public final class SphereProblem extends ParallelProblem<Double> {

	private static final double DEFAULT_RANGE = 5.12;

	/**
	 * Creates a SphereProblem with a default range of [-5.12, 5.12].
	 *
	 * @param dimension
	 * 		The dimension of this problem
	 */
	public SphereProblem(final int dimension) {
		super(dimension, -DEFAULT_RANGE, DEFAULT_RANGE);
	}
}
