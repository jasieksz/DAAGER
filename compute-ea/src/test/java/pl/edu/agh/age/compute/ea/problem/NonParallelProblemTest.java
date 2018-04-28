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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for NonParallelProblem.
 *
 * @author AGH AgE Team
 */
public class NonParallelProblemTest {

	private NonParallelProblem<Integer> problem;

	private final int dimension = 3;

	private final Integer[] min = {-1, -2, 0};

	private final Integer[] max = {1, 0, 2};

	@Before public void setUp() {
		problem = new NonParallelProblem<Integer>(min, max);
	}

	@Test public void testDimension() {
		Assert.assertEquals(dimension, problem.dimension());
	}

	@Test(expected = IllegalArgumentException.class) public void testNegativeLowerBound() {
		problem.lowerBound(-1);
	}

	@Test(expected = IllegalArgumentException.class) public void testTooHighLowerBound() {
		problem.upperBound(dimension);
	}

	@Test public void testLowerBound() {
		assertThat(problem.lowerBound(0)).isEqualTo(min[0]);
		assertThat(problem.lowerBound(1)).isEqualTo(min[1]);
		assertThat(problem.lowerBound(2)).isEqualTo(min[2]);
	}

	@Test(expected = IllegalArgumentException.class) public void testNegativeUpperBound() {
		problem.upperBound(-1);
	}

	@Test(expected = IllegalArgumentException.class) public void testTooHighUpperBound() {
		problem.upperBound(dimension);
	}

	@Test public void testUpperBound() {
		assertThat(problem.upperBound(0)).isEqualTo(max[0]);
		assertThat(problem.upperBound(1)).isEqualTo(max[1]);
		assertThat(problem.upperBound(2)).isEqualTo(max[2]);
	}
}
