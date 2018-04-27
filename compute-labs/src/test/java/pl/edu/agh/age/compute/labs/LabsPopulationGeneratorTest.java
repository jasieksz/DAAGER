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
