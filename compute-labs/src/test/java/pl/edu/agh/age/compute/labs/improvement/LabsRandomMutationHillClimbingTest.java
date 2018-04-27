package pl.edu.agh.age.compute.labs.improvement;


import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Test;

public class LabsRandomMutationHillClimbingTest {

	private static final int numberOfIterations = 20;

	private final LabsEvaluator evaluator = new LabsEvaluator(EvaluatorCounter.empty());

	@Test public void testRMHC() {
		// given
		final LabsRandomMutationHillClimbing improvement = new LabsRandomMutationHillClimbing(evaluator,
		                                                                                      numberOfIterations, true);
		LabsSolution solution = new LabsSolution(
			new boolean[] {true, false, true, false, true, false, true, true, false});
		solution = solution.withFitness(evaluator.evaluate(solution));

		// when
		final LabsSolution result = improvement.improve(solution);

		// then
		int differentBits = 0;
		for (int i = 0; i < solution.length(); i++) {
			if (solution.sequenceRepresentation()[i] != result.sequenceRepresentation()[i]) {
				differentBits++;
			}
		}

		assertThat(numberOfIterations >= differentBits).isTrue();
		assertThat(solution.fitnessValue() <= result.fitnessValue()).isTrue();
	}
}
