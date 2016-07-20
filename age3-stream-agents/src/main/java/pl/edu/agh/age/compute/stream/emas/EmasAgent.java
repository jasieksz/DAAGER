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

/*
 * Created: 2016-07-11.
 */

package pl.edu.agh.age.compute.stream.emas;

import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of agent for EMAS.
 *
 * This type of agent requires identity (thus we have ID field).
 *
 * EMAS model requires that an agent is described by:
 * * its energy,
 * * its solution.
 *
 * This implementation is immutable and untyped in regards to the solution instance.
 */
@SuppressWarnings("PublicField")
public final class EmasAgent implements Agent {

	private static final long serialVersionUID = -5131978693617912709L;

	// FIXME: 7/20/2016 kpietak: UUID seems to be heavy when we compare it to other agent's attributes. We should maybe consider something lighter.
	public final UUID id;

	public final double energy;

	public final Solution<?> solution;

	private EmasAgent(final double energy, final Solution<?> solution) {
		this(UUID.randomUUID(), energy, solution);
	}

	private EmasAgent(final UUID id, final double energy, final Solution<?> solution) {
		this.id = id;
		this.energy = energy;
		this.solution = solution;
	}

	/**
	 * Creates a new agent.
	 *
	 * @param energy
	 * 		initial energy
	 * @param solution
	 * 		initial solution
	 *
	 * @return a new agent instance
	 */
	public static EmasAgent create(final double energy, final Solution<?> solution) {
		return new EmasAgent(energy, solution);
	}

	/**
	 * Create a copy of this agent with a new energy value.
	 *
	 * The agent identity is preserved.
	 *
	 * @param newEnergy
	 * 		new energy for this agent
	 *
	 * @return a copy of this agent
	 */
	public EmasAgent withEnergy(final double newEnergy) {
		return new EmasAgent(id, newEnergy, solution);
	}

	/**
	 * Create a copy of this agent with a new solution value.
	 *
	 * The agent identity is preserved.
	 *
	 * @param newSolution
	 * 		new solution for this agent
	 *
	 * @return a copy of this agent
	 */
	public EmasAgent withSolution(final Solution<?> newSolution) {
		return new EmasAgent(id, energy, newSolution);
	}

	@Override public int hashCode() {
		return Objects.hash(id);
	}

	@Override public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		final EmasAgent emasAgent = (EmasAgent)o;
		return Objects.equals(id, emasAgent.id);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this)
		                  .add("id", id)
		                  .add("energy", energy)
		                  .add("solution", solution)
		                  .toString();
	}
}
