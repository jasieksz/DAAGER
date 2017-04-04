/*
 * Copyright (C) 2016-2017 Intelligent Information Systems Group.
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

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.emas.reproduction.mutation.Mutation;
import pl.edu.agh.age.compute.stream.emas.reproduction.transfer.AsexualEnergyTransfer;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

/**
 * Builder for building {@link AsexualReproduction} functions.
 *
 * @param <S>
 * 		type of the solution
 */
public final class AsexualReproductionBuilder<S extends Solution<?>> {

	private Mutation<S> mutation;

	private AsexualEnergyTransfer energyTransfer;

	AsexualReproductionBuilder() {}

	public AsexualReproductionBuilder<S> withMutation(final Mutation<S> mutation) {
		this.mutation = requireNonNull(mutation);
		return this;
	}

	public AsexualReproductionBuilder<S> withEnergyTransfer(final AsexualEnergyTransfer energyTransfer) {
		this.energyTransfer = requireNonNull(energyTransfer);
		return this;
	}

	public AsexualReproduction build() {
		// Energy transfer is always required (to create a new child)
		checkState(energyTransfer != null);

		return parent -> {
			AsexualReproductionPipeline<S> pipeline = AsexualReproductionPipeline.on(parent);
			pipeline = pipeline.recombine();
			if (mutation != null) {
				pipeline = pipeline.mutate(mutation);
			}
			pipeline = pipeline.transferEnergy(energyTransfer);
			return pipeline.extract();
		};
	}
}
