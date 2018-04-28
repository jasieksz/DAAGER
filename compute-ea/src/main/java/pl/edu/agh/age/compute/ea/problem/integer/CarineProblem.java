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


package pl.edu.agh.age.compute.ea.problem.integer;


import pl.edu.agh.age.compute.ea.problem.NonParallelProblem;

/**
 * Defines the default bound for a CarineEvaluator.
 */
public final class CarineProblem extends NonParallelProblem<Integer> {

	/**
	 * Creates an CarineProblem with default bounds of [0, 0, 1, 0, 1, 1, 0, 0]x[1, 30, 50000, 255, 48, 64, 63, 10].
	 */
	public CarineProblem() {
		super(new Integer[] {0, 0, 1, 0, 1, 1, 0, 0}, new Integer[] {1, 30, 50000, 255, 48, 64, 63, 10});
	}
}
