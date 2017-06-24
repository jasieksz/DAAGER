package pl.edu.agh.age.compute.stream.emas.migration;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Selectors;

import java.util.function.BiFunction;

import io.vavr.collection.List;

/**
 * The strategies for agents migration.
 *
 * Deterministic strategies (random, highest fitness and lowest fitness) are quite obvious in their behavior.
 * Probabilistic strategies choose agents based on their evaluation quality. Agents with a better evaluation have higher
 * probability of being chosen, and agents with a worse one - have smaller probability. There is no guarantee that agent
 * with the best evaluation of all available will be chosen in probabilistic strategies.
 *
 * IMPORTANT NOTICE - probabilistic selectors behave best when fitness values consist of only positive or only negative
 * numbers (excluding zeros). If the solution evaluator can output both positive and negative fitnesses - consider using
 * non-probabilistic selectors for more accurate selection, as the probabilistic ones tend to have rather uniform
 * probability distribution in such situations.
 */
public enum MigrationStrategy {

	RANDOM(Selectors.random()),
	HIGHEST_FITNESS(Selectors.highestFitness()),
	HIGHEST_FITNESS_PROBABILISTIC(Selectors.highestFitnessProbabilistic()),
	LOWEST_FITNESS(Selectors.lowestFitness()),
	LOWEST_FITNESS_PROBABILISTIC(Selectors.lowestFitnessProbabilistic());

	private final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector;

	MigrationStrategy(final BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector) {
		this.selector = selector;
	}

	public BiFunction<List<EmasAgent>, Integer, List<EmasAgent>> selector() {
		return selector;
	}

}
