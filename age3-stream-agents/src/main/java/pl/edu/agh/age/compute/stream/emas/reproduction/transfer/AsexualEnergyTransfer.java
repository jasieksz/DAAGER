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
 * Energy transfer operator for asexual reproduction. Operates on doubles.
 */
@FunctionalInterface
public interface AsexualEnergyTransfer {

	/**
	 * @param parent
	 * 		parent agent
	 *
	 * @return array with energy values for the parent and a child
	 */
	double[] transfer(EmasAgent parent);

	/**
	 * Returns an energy transfer operator that distributes energy equally between all two agents.
	 */
	static AsexualEnergyTransfer equal() {
		return (parent) -> {
			final double v = parent.energy / 2.0;
			return new double[] {v, v};
		};
	}

	/**
	 * Returns an energy transfer operator that distributes energy in proportional energy portions.
	 *
	 * @param proportion
	 * 		proportion of parent's energy that will be transfered to the child
	 * @param minimumAgentEnergy
	 * 		minimum agent energy that agent can have
	 */
	static AsexualEnergyTransfer proportional(final double proportion, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(proportion >= 0 && proportion <= 1, "Proportion value is out of allowed range");
		return (parent) -> {
			final double childEnergy = parent.energy * proportion;
			final double parentEnergy = parent.energy * (1 - proportion);
			if (parentEnergy <= minimumAgentEnergy) {
				return new double[] {0.0, parent.energy};
			}
			return new double[] {parentEnergy, childEnergy};
		};
	}

	/**
	 * Returns an energy transfer operator that distributes energy in fixed portions.
	 *
	 * @param transferredEnergy
	 * 		the transferred energy portion
	 * @param minimumAgentEnergy
	 * 		the minimum agent energy that agent can have
	 */
	static AsexualEnergyTransfer constant(final double transferredEnergy, final double minimumAgentEnergy) {
		// FIXME: minimumAgentEnergy does not fit here - it's not the responsibility of transfer to kill parents
		checkArgument(transferredEnergy >= 0);
		return (parent) -> {
			final double parentEnergy = parent.energy - transferredEnergy;
			if (parentEnergy <= minimumAgentEnergy) {
				return new double[] {0.0, parent.energy};
			}
			return new double[] {parentEnergy, transferredEnergy};
		};
	}
}
