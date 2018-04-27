package pl.edu.agh.age.compute.labs.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import pl.edu.agh.age.compute.labs.LabsSequences;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Before;
import org.junit.Test;

public class LabsEvaluatorWithCacheTest {
	private static final double ASSERTION_DELTA = 0.001;

	private LabsEvaluator evaluator;

	@Before public void setUp() {
		evaluator = new LabsEvaluatorWithCache(EvaluatorCounter.empty(), 100);
	}

	@Test public void testLabsLength4Evaluator() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length4MeritFactor(), within(ASSERTION_DELTA));
	}

	@Test public void testLabsLength4EvaluatorTwiceTheSameSolution() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();
		evaluator.evaluate(sequence);

		// When
		final double meritFactor = evaluator.evaluate(sequence);

		// Then
		assertThat(meritFactor).isCloseTo(LabsSequences.length4MeritFactor(), within(ASSERTION_DELTA));
	}

}
