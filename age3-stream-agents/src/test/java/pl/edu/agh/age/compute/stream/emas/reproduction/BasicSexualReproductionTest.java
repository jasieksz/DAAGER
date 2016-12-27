package pl.edu.agh.age.compute.stream.emas.reproduction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.reproduction.improvement.Improvement;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleSolution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Seq;

@RunWith(MockitoJUnitRunner.class)
public final class BasicSexualReproductionTest {

	@Mock private Recombination<DoubleSolution> recombination;

	@Mock private Mutation<DoubleSolution> mutation;

	@Mock private EnergyTransfer energyTransfer;

	@Mock private Evaluator<DoubleSolution> evaluator;

	@Mock private Improvement<DoubleSolution> improvement;

	private final EmasAgent firstParent = EmasAgent.create(1.0, new DoubleSolution(10, 11));

	private final EmasAgent secondParent = EmasAgent.create(1.0, new DoubleSolution(20, 12));

	private final Tuple2<EmasAgent, EmasAgent> parents = Tuple.of(firstParent, secondParent);

	private SexualReproductionBuilder<DoubleSolution> builder = null;

	@Before public void setUp() {
		builder = SexualReproduction.builder();
	}

	@Test public void testReproductionFlow() {
		// given
		final SexualReproduction reproduction = builder.withMutation(mutation)
		                                               .withRecombination(recombination)
		                                               .withEnergyTransfer(energyTransfer)
		                                               .withEvaluator(evaluator)
		                                               .withImprovement(improvement)
		                                               .build();
		given(energyTransfer.transfer(firstParent, secondParent)).willReturn(new double[] {0.0, 0.0, 0.0});
		given(mutation.mutate(any())).willReturn(new DoubleSolution(1));
		given(recombination.recombine((DoubleSolution)firstParent.solution,
		                              (DoubleSolution)secondParent.solution)).willReturn(new DoubleSolution(2));
		given(evaluator.evaluate(any())).willReturn(1.0);
		given(improvement.improve(any())).willReturn(new DoubleSolution(3, 13));

		// when
		final Seq<EmasAgent> newAgents = reproduction.apply(parents);

		// then
		then(recombination).should(times(1)).recombine(any(), any());
		then(mutation).should(times(1)).mutate(any());
		then(energyTransfer).should(times(1)).transfer(parents._1, parents._2);
		then(evaluator).should(times(1)).evaluate(any());
		then(improvement).should(times(1)).improve(any());

		assertThat(newAgents).hasSize(3)
		                     .extracting(emasAgent -> emasAgent.solution.fitnessValue())
		                     .contains(11.0, 12.0, 13.0);
	}

	@Test public void shouldRequireRecombination() {
		// given
		builder.withMutation(mutation)
		       //.withRecombination(recombination)
		       .withEnergyTransfer(energyTransfer).withEvaluator(evaluator);

		// when
		assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
	}

	@Test public void shouldRequireEnergyTransfer() {
		// given
		builder.withMutation(mutation).withRecombination(recombination)
		       //.withEnergyTransfer(energyTransfer)
		       .withEvaluator(evaluator);

		// when
		assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
	}

	@Test public void shouldRequireEvaluator() {
		// given
		builder.withMutation(mutation).withRecombination(recombination).withEnergyTransfer(energyTransfer);
		//.withEvaluator(evaluator);

		// when
		assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
	}
}
