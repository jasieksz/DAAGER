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

package pl.edu.agh.age.compute.stream.emas.fight.transfer;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;

@FunctionalInterface
public interface FightEnergyTransfer {
	/**
	 * Transfers the energy between fighting agents
	 *
	 * @param loser
	 * 		losing agent
	 * @param winner
	 * 		winning agent
	 *
	 * @return array with energy values for losing agent and winning agent
	 */
	double[] transfer(EmasAgent loser, EmasAgent winner);

	/**
	 * Returns an energy transfer operator that distributes energy of the loser proportionally between agents.
	 *
	 * @param proportion
	 * 		proportion of energy that winning agent should receive
	 * @param minimumAgentEnergy
	 * 		minimum agent energy that agent can have
	 */
	static FightEnergyTransfer proportional(final double proportion, final double minimumAgentEnergy) {
		checkArgument((proportion >= 0) && (proportion <= 1), "Proportion value is out of allowed range");
		return (loser, winner) -> {
			// If agent dies after the fight -> transfer all his energy to the winner
			final double delta = ((loser.energy * (1 - proportion)) <= minimumAgentEnergy)
			                     ? loser.energy
			                     : (loser.energy * proportion);
			return new double[] {loser.energy - delta, winner.energy + delta};
		};
	}

}
