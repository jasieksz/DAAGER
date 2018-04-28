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


package pl.edu.agh.age.compute.ea.rand;

/**
 * This interface introduces additional semantics to {@link DoubleRandomGenerator}.
 *
 * It is intended for random distributions which take two additional parameters, a location and a scale. The statistical
 * interpretation of these parameters is left to implementations.
 *
 * The {@code nextDouble()} method is now assumed to use some default values for these parameters.
 */
public interface DoubleSymmetricGenerator extends DoubleRandomGenerator {

	/**
	 * Returns a random double value arbitrarily distributed in the range
	 * {@code [lowerDouble(), upperDouble()]}, accordingly to the provided parameters.
	 *
	 * @param location
	 * 		The location of the distribution.
	 * @param scale
	 * 		The scale of the distribution
	 *
	 * @return A random value.
	 */
	double nextDouble(double location, double scale);
}
