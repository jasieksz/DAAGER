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
 * This class represents the problem domain for a floating-point coded Schwefel function.
 * http://www-optima.amp.i.kyoto-u.ac.jp/member/student/hedar/Hedar_files/TestGO_files/Page2530.htm
 * Default range: [-500, 500]
 */
public final class SchwefelProblem extends ParallelProblem<Double> {

	private static final double DEFAULT_RANGE = 500;

	/**
	 * Creates a SchwefelProblem with a default range of [-500, 500].
	 *
	 * @param dimension
	 * 		The dimension of this problem
	 */
	public SchwefelProblem(final int dimension) {
		super(dimension, -DEFAULT_RANGE, DEFAULT_RANGE);
	}
}
