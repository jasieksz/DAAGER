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

package pl.edu.agh.age.compute.stream.emas.reproduction.transfer;


import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;

/**
 * Energy transfer operator for sexual reproduction. Operates on doubles.
 */
@FunctionalInterface
public interface EnergyTransfer {

	/**
	 * @param first
	 * 		parent agent
	 * @param second
	 * 		parent agent
	 *
	 * @return array with energy values for first parent, second parent, child
	 */
	double[] transfer(EmasAgent first, EmasAgent second);

	/**
	 * Returns an energy transfer operator that distributes energy equally between all three agents.
	 */
	static EnergyTransfer equal() {
		return (first, second) -> {
			final double v = (first.energy + second.energy) / 3.0;
			return new double[] {v, v, v};
		};
	}

	/**
	 * Returns an energy transfer operator that distributes energy in proportional energy portions.
	 *
	 * @param proportion
	 * 		proportion of each parent's energy that will be transfered to the child
	 * @param minimumAgentEnergy
	 * 		minimum agent energy that agent can have
	 */
	static EnergyTransfer proportional(final double proportion, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(proportion >= 0 && proportion <= 1, "Proportion value is out of allowed range");
		return (first, second) -> {
			final double firstParentGift = first.energy * proportion;
			final double firstParentEnergy = first.energy * (1 - proportion);
			final double secondParentGift = second.energy * proportion;
			final double secondParentEnergy = second.energy * (1 - proportion);
			if (firstParentEnergy <= minimumAgentEnergy || secondParentEnergy <= minimumAgentEnergy) {
				return new double[] {0.0, 0.0, first.energy + second.energy};
			}
			return new double[] {firstParentEnergy, secondParentEnergy, firstParentGift + secondParentGift};
		};
	}

	/**
	 * Returns an energy transfer operator that distributes energy in fixed portions.
	 *
	 * @param transferredEnergy
	 *        the transferred energy portion (each parent will lose energy portion of this value and a child will gain
	 *        twice as much energy)
	 * @param minimumAgentEnergy
	 *        the minimum agent energy that agent can have
	 */
	static EnergyTransfer constant(final double transferredEnergy, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(transferredEnergy >= 0);
		return (first, second) -> {
			final double firstParentEnergy = first.energy - transferredEnergy;
			final double secondParentEnergy = second.energy - transferredEnergy;
			if (firstParentEnergy <= minimumAgentEnergy || secondParentEnergy <= minimumAgentEnergy) {
				return new double[] {0.0, 0.0, first.energy + second.energy};
			}
			return new double[] {firstParentEnergy, secondParentEnergy, 2 * transferredEnergy};
		};
	}
}
