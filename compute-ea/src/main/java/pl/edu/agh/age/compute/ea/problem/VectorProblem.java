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


package pl.edu.agh.age.compute.ea.problem;

/**
 * Interface for vector problems, i.e. problems which have some number of dimensions and each one is bounded.
 *
 * @param <R>
 * 		The type of the problem bounds
 */
public interface VectorProblem<R> {

	/**
	 * Returns the problem's dimension.
	 *
	 * @return the problem's dimension.
	 */
	int dimension();

	/**
	 * Returns the problem's lower bound in a given dimension, indexed from 0. It must be not greater than the
	 * {@link #upperBound(int)} in the same dimension.
	 *
	 * @param atDimension
	 * 		the given dimension
	 *
	 * @return the problem's lower bound in the given dimension
	 *
	 * @throws IllegalArgumentException
	 * 		if the given dimension is greater than this problem's one or negative
	 */
	R lowerBound(int atDimension);

	/**
	 * Returns the problem's upper bound in a given dimension, indexed from 0. It must be not smaller than the
	 * {@link #lowerBound(int)} in the same dimension.
	 *
	 * @param atDimension
	 * 		the given dimension
	 *
	 * @return the problem's upper bound in the given dimension
	 *
	 * @throws IllegalArgumentException
	 * 		if the given dimension is greater than this problem's one or negative
	 */
	R upperBound(int atDimension);
}
