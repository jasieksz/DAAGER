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

package pl.edu.agh.age.compute.stream.emas.reproduction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.EnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.DoubleSolution;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;

@RunWith(MockitoJUnitRunner.class)
public final class BasicSexualReproductionTest {

	@Mock private Recombination<DoubleSolution> recombination;

	@Mock private Mutation<DoubleSolution> mutation;

	@Mock private EnergyTransfer energyTransfer;

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
		                                               .build();
		given(energyTransfer.transfer(firstParent, secondParent)).willReturn(new double[] {0.0, 0.0, 0.0});
		given(mutation.mutate(any())).willReturn(new DoubleSolution(1));
		given(recombination.recombine((DoubleSolution)firstParent.solution,
		                              (DoubleSolution)secondParent.solution)).willReturn(new DoubleSolution(2));

		// when
		final Tuple2<Seq<EmasAgent>, EmasAgent> agents = reproduction.apply(parents);

		// then
		then(recombination).should(times(1)).recombine(any(), any());
		then(mutation).should(times(1)).mutate(any());
		then(energyTransfer).should(times(1)).transfer(parents._1, parents._2);

		assertThat(agents._1).hasSize(2)
		                     .extracting(emasAgent -> emasAgent.solution.fitnessValue())
		                     .contains(11.0, 12.0);
		assertThat(agents._2).extracting(emasAgent -> emasAgent.solution.fitnessValue())
		                     .contains(Double.NaN); // child is not yet evaluated at this point
	}

	@Test public void shouldRequireRecombination() {
		// given
		builder.withMutation(mutation)
		       //.withRecombination(recombination)
		       .withEnergyTransfer(energyTransfer);

		// when
		assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
	}

	@Test public void shouldRequireEnergyTransfer() {
		// given
		builder.withMutation(mutation)
		       .withRecombination(recombination);
		       //.withEnergyTransfer(energyTransfer)

		// when
		assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
	}
}
