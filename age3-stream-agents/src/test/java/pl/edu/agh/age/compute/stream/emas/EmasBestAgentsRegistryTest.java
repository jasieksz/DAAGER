/*
 * Copyright (C) 2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.stream.emas;

import pl.edu.agh.age.compute.stream.emas.solution.SimpleSolution;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.OptionalDouble;

import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.collection.List;
import javaslang.collection.Map;

/*
 * Created: 2016-11-21.
 */
public final class EmasBestAgentsRegistryTest {

	@Rule public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

	private EmasBestAgentsRegistry registry;

	@Before public void setUp() {
		registry = new EmasBestAgentsRegistry(EmasAgentComparators.higherFitness());
	}

	@Test public void testIfRegistersCorrectly() {
		final List<EmasAgent> agents = List.of(EmasAgent.create(0.1, new SimpleSolution<>(1, 1.0)),
		                                       EmasAgent.create(0.1, new SimpleSolution<>(1, 2.0)),
		                                       EmasAgent.create(0.1, new SimpleSolution<>(1, 0.5)));
		registry.register(1, 1, agents);

		final OptionalDouble bestAgentEvaluation1 = registry.getBestAgentEvaluation();
		softly.assertThat(bestAgentEvaluation1).isPresent().hasValue(2.0);

		final List<EmasAgent> nextAgents = List.of(EmasAgent.create(0.1, new SimpleSolution<>(1, 1.0)),
		                                           EmasAgent.create(0.1, new SimpleSolution<>(1, 2.0)),
		                                           EmasAgent.create(0.1, new SimpleSolution<>(1, 2.0)),
		                                           EmasAgent.create(0.1, new SimpleSolution<>(1, 0.5)));

		registry.register(2, 2, nextAgents);

		final OptionalDouble bestAgentEvaluation2 = registry.getBestAgentEvaluation();
		softly.assertThat(bestAgentEvaluation2).isPresent().hasValue(2.0);

		final Map<String, Tuple3<Long, Long, Long>> bestAgentsStatistics = registry.getBestAgentsStatistics();
		final Tuple3<Long, Long, Long> statistics = bestAgentsStatistics.values().get();

		softly.assertThat(statistics).isEqualTo(Tuple.of(1L, 1L, 3L));

	}

}
