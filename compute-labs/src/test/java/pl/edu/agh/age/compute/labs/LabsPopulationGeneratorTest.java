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

package pl.edu.agh.age.compute.labs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.labs.solution.LabsSolutionFactory;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public final class LabsPopulationGeneratorTest {

	private static final int numberOfAgents = 100;

	private static final double initialEnergy = 40.0;

	private final boolean[] sampleSequence = {true, false, true};

	private LabsSolutionFactory solutionFactory;

	@Before public void setUp() {
		solutionFactory = mock(LabsSolutionFactory.class);
		when(solutionFactory.create()).thenReturn(new LabsSolution(sampleSequence));
	}

	@Test public void testProperNumberOfAgentsIsReturned() {
		// given
		final LabsPopulationGenerator generator = new LabsPopulationGenerator(solutionFactory, numberOfAgents,
		                                                                      initialEnergy);

		// when
		final List<EmasAgent> agents = generator.createPopulation();

		// then
		assertThat(agents.size()).isEqualTo(numberOfAgents);
	}

	@Test public void testProperAmountOfEnergy() {
		// given
		final LabsPopulationGenerator generator = new LabsPopulationGenerator(solutionFactory, numberOfAgents,
		                                                                      initialEnergy);

		// when
		final List<EmasAgent> agents = generator.createPopulation();

		// then
		for (final EmasAgent agent : agents) {
			assertThat(agent.energy).isEqualTo(initialEnergy, within(0.001));
		}
	}

	@Test public void testProperAgentsAreProduced() {
		// given
		final LabsPopulationGenerator generator = new LabsPopulationGenerator(solutionFactory, numberOfAgents,
		                                                                      initialEnergy);

		// when
		final List<EmasAgent> agents = generator.createPopulation();

		// then
		for (final EmasAgent agent : agents) {
			assertThat(((LabsSolution)agent.solution).sequenceRepresentation()).containsExactly(sampleSequence);
		}
	}

	@Test public void testInvalidNumberOfAgents() {
		assertThatThrownBy(() -> new LabsPopulationGenerator(solutionFactory, 0, initialEnergy)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

	@Test public void testInvalidAmountOfEnergy() {
		assertThatThrownBy(() -> new LabsPopulationGenerator(solutionFactory, 1, 0.0)).isExactlyInstanceOf(
			IllegalArgumentException.class);
	}

}
