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
	 * Returns an energy transfer operator that distributes energy in proportional energy portions.
	 *
	 * @param proportion
	 * 		proportion of energy that winning agent should receive
	 * @param minimumAgentEnergy
	 * 		minimum agent energy that agent can have
	 */
	static FightEnergyTransfer proportional(final double proportion, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(proportion >= 0 && proportion <= 1, "Proportion value is out of allowed range");
		return (loser, winner) -> {
			// If agent dies after the fight -> transfer all his energy to the winner
			final double delta = (loser.energy * (1 - proportion) <= minimumAgentEnergy)
			                     ? loser.energy
			                     : (loser.energy * proportion);
			return new double[] {loser.energy - delta, winner.energy + delta};
		};
	}

	/**
	 * Returns an energy transfer operator that distributes energy in fixed portions.
	 *
	 * @param transferredEnergy
	 *        the transferred energy portion
	 * @param minimumAgentEnergy
	 *        the minimum agent energy that agent can have
	 */
	static FightEnergyTransfer constant(final double transferredEnergy, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(transferredEnergy >= 0);
		return (loser, winner) -> {
			// if agent dies after the fight -> transfer all his energy to the winner
			final double delta = ((loser.energy - transferredEnergy) <= minimumAgentEnergy)
				                 ? loser.energy : transferredEnergy;
			return new double[] {loser.energy - delta, winner.energy + delta};
		};
	}

}
