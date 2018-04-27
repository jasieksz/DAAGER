package pl.edu.agh.age.compute.labs.improvement;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import pl.edu.agh.age.compute.labs.evaluator.LabsEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

import org.junit.Test;

import java.util.Arrays;

public class LabsSteepestDescentLocalSearchTest {
	final LabsEvaluator evaluator = new LabsEvaluator(EvaluatorCounter.empty());

	@Test public void testSDLS() {
		// given
		LabsSteepestDescentLocalSearch improvement = new LabsSteepestDescentLocalSearch(evaluator, true);
		LabsSolution solution = new LabsSolution(new boolean[] {true, true, true, true, true});
		solution = solution.withFitness(evaluator.evaluate(solution));

		// when
		LabsSolution result = improvement.improve(solution);

		// then
		boolean[] exp1 = {true, false, true, true, true};
		boolean[] exp2 = {true, false, true, true, true};
		assertThat(Arrays.equals(exp1, result.sequenceRepresentation()) || Arrays.equals(exp2,
		                                                                                 result.sequenceRepresentation()))
			.isTrue();
		assertThat(result.fitnessValue()).isEqualTo(evaluator.evaluate(new LabsSolution(exp1)), within(0.001));
	}
}
