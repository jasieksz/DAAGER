package pl.edu.agh.age.compute.stream.emas.reproduction;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import javaslang.Tuple;
import javaslang.Tuple2;

@SuppressWarnings({"InstanceVariableMayNotBeInitialized", "ReturnValueIgnored"})
@RunWith(MockitoJUnitRunner.class)
public final class BasicSexualReproductionTest {

	@Mock private Recombination<Solution<?>> recombination;

	@Mock private Mutation<Solution<?>> mutation;

	@Mock private EnergyTransfer energyTransfer;

	@Mock private Evaluator<Solution<?>> evaluator;

	private final EmasAgent firstParent = EmasAgent.create(1.0, mock(Solution.class));

	private final EmasAgent secondParent = EmasAgent.create(1.0, mock(Solution.class));

	private final Tuple2<EmasAgent, EmasAgent> parents = Tuple.of(firstParent, secondParent);

	@Test public void testReproductionFlow() {
		// given
		final SexualReproduction reproduction = SexualReproduction.builder()
		                                                          .withMutation(mutation)
		                                                          .withRecombination(recombination)
		                                                          .withEnergyTransfer(energyTransfer)
		                                                          .withEvaluator(evaluator)
		                                                          .build();
		given(energyTransfer.transfer(firstParent, secondParent)).willReturn(new double[] {0.0, 0.0, 0.0});
		given(mutation.mutate(any())).willReturn(mock(Solution.class));
		given(recombination.recombine(firstParent.solution, secondParent.solution)).willReturn(mock(Solution.class));
		given(evaluator.evaluate(any())).willReturn(1.0);

		// when
		reproduction.apply(parents);

		// then
		then(recombination).should(times(1)).recombine(any(), any());
		then(mutation).should(times(1)).mutate(any());
		then(energyTransfer).should(times(1)).transfer(parents._1, parents._2);
	}

}
