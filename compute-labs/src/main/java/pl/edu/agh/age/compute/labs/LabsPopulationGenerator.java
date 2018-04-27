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

package pl.edu.agh.age.compute.labs;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.labs.solution.LabsSolutionFactory;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.PopulationGenerator;

import java.util.List;

import io.vavr.collection.Stream;

public final class LabsPopulationGenerator implements PopulationGenerator<EmasAgent> {

	private final LabsSolutionFactory solutionFactory;

	private final int agentsCount;

	private final double initialAgentEnergy;

	public LabsPopulationGenerator(final LabsSolutionFactory solutionFactory, final int agentsCount,
	                               final double initialAgentEnergy) {
		checkArgument(agentsCount > 0);
		checkArgument(initialAgentEnergy > 0);

		this.solutionFactory = requireNonNull(solutionFactory);
		this.agentsCount = agentsCount;
		this.initialAgentEnergy = initialAgentEnergy;
	}

	@Override public List<EmasAgent> createPopulation() {
		return Stream.range(0, agentsCount).map(i -> createAgent()).toJavaList();
	}

	private EmasAgent createAgent() {
		final LabsSolution solution = solutionFactory.create();
		return EmasAgent.create(initialAgentEnergy, solution);
	}

}
