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

package pl.edu.agh.age.compute.stream.emas.reproduction.transfer;


import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;

/**
 * Energy transfer operator. Operates on doubles.
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
	 * Returns an energy transfer operator that distributes energy proportionally with given proportions between all
	 * three agents.
	 *
	 * @param parentsProportion
	 * 		proportion of energy that parents should receive
	 * @param childProportion
	 * 		proportion of energy that child should receive
	 * @param minimumAgentEnergy
	 * 		minimum agent energy that agent can have
	 */
	static EnergyTransfer proportional(final double parentsProportion, final double childProportion,
	                                   final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument((parentsProportion >= 0) && (parentsProportion <= 1));
		checkArgument((childProportion >= 0) && (childProportion <= 1));
		checkArgument(Math.abs(parentsProportion + childProportion - 1) <= 0.0001,
		              "Proportion sum is not equal to 1.0");
		return (first, second) -> {
			final double energySum = first.energy + second.energy;
			final double parentEnergy = (energySum * parentsProportion) / 2.0;
			final double childEnergy = energySum * childProportion;
			// XXX: really for childEnergy <= minimum?
			if ((parentEnergy <= minimumAgentEnergy) || (childEnergy <= minimumAgentEnergy)) {
				return new double[] {0.0, 0.0, energySum};
			}
			return new double[] {parentEnergy, parentEnergy, childEnergy};
		};
	}
}
