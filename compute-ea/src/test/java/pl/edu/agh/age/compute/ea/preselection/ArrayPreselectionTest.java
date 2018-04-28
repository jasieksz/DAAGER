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

package pl.edu.agh.age.compute.ea.preselection;

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import io.vavr.collection.List;
import io.vavr.collection.Stream;

public class ArrayPreselectionTest {

	private ArrayPreselection<Double, DoubleVectorSolution> preselection;


	@Before public void setUp() {
		preselection = new MockArrayPreselection();
	}

	@Test(expected = NullPointerException.class) public void shouldThrowExceptionForNullPopulation() {
		// given
		final List<DoubleVectorSolution> population = null;

		// when
		preselection.preselect(population);
	}

	@Test public void shouldReturnEmptyForEmptyPopulation() {
		// given
		final List<DoubleVectorSolution> population = List.empty();

		// when
		final List<DoubleVectorSolution> preselectedPopulation = preselection.preselect(population);

		// then
		assertThat(preselectedPopulation).isEmpty();
	}

	@Test public void shouldReturnSameWhenOneSolution() {
		// given
		final DoubleVectorSolution solution = new DoubleVectorSolution(new double[2]);
		final List<DoubleVectorSolution> population = List.of(solution);

		// when
		final List<DoubleVectorSolution> preselectedPopulation = preselection.preselect(population);

		// then
		assertThat(preselectedPopulation).hasSize(1);
		assertThat(preselectedPopulation).contains(solution);
	}

	private static final class MockArrayPreselection extends ArrayPreselection<Double, DoubleVectorSolution> {

		@Override protected int[] preselectIndices(final double[] values) {
			return ArrayUtils.toPrimitive(Stream.range(0, values.length).toJavaArray(Integer.class));
		}
	}

	// TODO test respect of indices
	// TODO test factory invocation

}
