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
