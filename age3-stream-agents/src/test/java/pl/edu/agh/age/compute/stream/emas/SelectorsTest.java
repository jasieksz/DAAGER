package pl.edu.agh.age.compute.stream.emas;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.function.BiFunction;

import io.vavr.collection.List;

public class SelectorsTest {

	private static final List<Double> POSITIVE_FITNESSES = //
	    List.ofAll(0.1, 0.4, 0.44, 0.59, 3.14, 3.33, 4.4, 5.0, 7.0, 8.6, 12.3, 30.0, 44.0, 66.0, 99.0);

	private static final List<Double> NEGATIVE_FITNESSES = //
	    List.ofAll(-0.1, -0.4, -0.44, -0.59, -3.14, -3.33, -4.4, -5.0, -7.0, -8.6, -12.3, -30.0, -44.0, -66.0, -99.0);

	/**
	 * See {@link MigrationStrategy} class comment for explanation about the poor behavior of probabilistic selectors
	 * for this set.
	 **/
	private static final List<Double> MIXED_FITNESSES = //
	    List.ofAll(0.0, -0.4, 0.44, -0.59, 3.14, -3.33, 4.4, -5.0, 7.0, -8.6, 12.3, -30.0, 44.0, -66.0, 99.0);

	private static final int AGENTS_TO_SELECT = 4;

	private static final int TEST_REPETITIONS = 10;

	private List<EmasAgent> positiveFitnessAgents;

	private List<EmasAgent> negativeFitnessAgents;

	private List<EmasAgent> mixedFitnessAgents;

	@Before public void before() {
		positiveFitnessAgents = generatePopulation(POSITIVE_FITNESSES.shuffle());
		negativeFitnessAgents = generatePopulation(NEGATIVE_FITNESSES.shuffle());
		mixedFitnessAgents = generatePopulation(MIXED_FITNESSES.shuffle());
	}

	private static List<EmasAgent> generatePopulation(final List<Double> fitnesses) {
		return fitnesses.map(fitness -> EmasAgentsComparatorTest.createAgentWithFitness(fitness)).toList();
	}

	@Test public void testRandomSelector() {
		final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector = Selectors.random();
		final String selectorName = "Random";
		testSelector(selector, selectorName, positiveFitnessAgents);
		testSelector(selector, selectorName, negativeFitnessAgents);
		testSelector(selector, selectorName, mixedFitnessAgents);
	}

	@Test public void testHighestFitnessSelector() {
		final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector = Selectors.highestFitness();
		final String selectorName = "Highest fitness";
		testSelector(selector, selectorName, positiveFitnessAgents, Optional.of(List.ofAll(30.0, 44.0, 66.0, 99.0)));
		testSelector(selector, selectorName, negativeFitnessAgents, Optional.of(List.ofAll(-0.1, -0.4, -0.44, -0.59)));
		testSelector(selector, selectorName, mixedFitnessAgents, Optional.of(List.ofAll(7.0, 12.3, 44.0, 99.0)));
	}

	@Test public void testLowestFitnessSelector() {
		final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector = Selectors.lowestFitness();
		final String selectorName = "Lowest fitness";
		testSelector(selector, selectorName, positiveFitnessAgents, Optional.of(List.ofAll(0.1, 0.4, 0.44, 0.59)));
		testSelector(selector, selectorName, negativeFitnessAgents, Optional.of(List.ofAll(-30.0, -44.0, -66.0, -99.0)));
		testSelector(selector, selectorName, mixedFitnessAgents, Optional.of(List.ofAll(-5.0, -8.6, -30.0, -66.0)));
	}

	@Test public void testHighestFitnessProbabilistic() {
		final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector = Selectors.highestFitnessProbabilistic();
		final String selectorName = "Highest fitness probabilistic";
		testProbabilisticSelector(selector, selectorName);
	}

	@Test public void testLowestFitnessProbabilistic() {
		final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector = Selectors.lowestFitnessProbabilistic();
		final String selectorName = "Lowest fitness probabilistic";
		testProbabilisticSelector(selector, selectorName);
	}

	private void testProbabilisticSelector(final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector,
	                                       final String selectorName) {
		testSelector(selector, selectorName, positiveFitnessAgents);
		testSelector(selector, selectorName, negativeFitnessAgents);
		testSelector(selector, selectorName, mixedFitnessAgents);
	}


	private void testSelector(final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector,
	                          final String selectorName, final List<EmasAgent> population) {
		testSelector(selector, selectorName, population, Optional.empty());
	}

	private void testSelector(final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector,
	                          final String selectorName, final List<EmasAgent> population,
	                          final Optional<List<Double>> expected) {
		final List<Double> expectedFitnesses = expected.isPresent() ? expected.get() : null;
		if (expectedFitnesses == null) {
			System.out.printf("%s selector test\n", selectorName);
			System.out.printf("original: %s\n", populationToString(population));
		}
		for (int i = 0; i < TEST_REPETITIONS; i++) {
			final List<EmasAgent> selectedAgents = selector.apply(population, AGENTS_TO_SELECT);
			final List<Double> selectedFitnesses = selectedAgents.map(agent -> agent.solution.fitnessValue());
			if (expectedFitnesses != null) {
				assertThat(expectedFitnesses.containsAll(selectedFitnesses) && selectedFitnesses.containsAll(
				    expectedFitnesses)).isTrue();
			} else {
				System.out.printf("selected: %s\n", populationToString(selectedAgents));
			}
		}
		if (expectedFitnesses == null) {
			System.out.println("--------------------------------------------");
		}
	}

	private static String populationToString(final List<EmasAgent> population) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[ ");
		for (int i = 0; i < population.size(); i++) {
			builder.append(String.format("%6.2f ", population.get(i).solution.fitnessValue()));
		}
		builder.append("]");
		return builder.toString();
	}

}
