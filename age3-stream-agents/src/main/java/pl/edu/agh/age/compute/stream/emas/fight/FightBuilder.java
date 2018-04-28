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

package pl.edu.agh.age.compute.stream.emas.fight;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.EmasAgentComparators;
import pl.edu.agh.age.compute.stream.emas.fight.transfer.FightEnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.Comparator;

import io.vavr.collection.List;
import io.vavr.collection.Seq;

@SuppressWarnings({"ParameterHidesMemberVariable", "InstanceVariableMayNotBeInitialized"})
public final class FightBuilder<S extends Solution<?>> {

	private Comparator<EmasAgent> agentComparator = EmasAgentComparators.higherFitness();

	private FightEnergyTransfer energyTransfer;

	FightBuilder() {}

	/**
	 * Agent comparator should return a non-negative integer if a first agent should win the fight
	 * or a negative integer if the second one should be the winner.
	 *
	 * @param agentComparator
	 * 		the agent comparator
	 *
	 * @return the fight builder
	 */
	public FightBuilder<S> withComparator(final Comparator<EmasAgent> agentComparator) {
		this.agentComparator = requireNonNull(agentComparator);
		return this;
	}

	public FightBuilder<S> withEnergyTransfer(final FightEnergyTransfer energyTransfer) {
		this.energyTransfer = requireNonNull(energyTransfer);
		return this;
	}

	public Fight build() {
		// Energy transfer is always required
		checkState(energyTransfer != null);

		return agents -> (agentComparator.compare(agents._1, agents._2) >= 0)
		                 ? transferEnergy(agents._2, agents._1) // first agent wins the fight
		                 : transferEnergy(agents._1, agents._2); // second agent wins the fight
	}

	private Seq<EmasAgent> transferEnergy(final EmasAgent loser, final EmasAgent winner) {
		final double[] energy = energyTransfer.transfer(loser, winner);
		return List.of(loser.withEnergy(energy[0]), winner.withEnergy(energy[1]));
	}

}
