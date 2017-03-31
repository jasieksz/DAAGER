package pl.edu.agh.age.compute.stream.emas.reproduction;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;

import java.util.function.Function;

import javaslang.Tuple2;

/**
 * Asexual reproduction is a function generating a new agent from a given one.
 * The returned tuple consist of 2 agents (a parent and a child).
 */
@FunctionalInterface
public interface AsexualReproduction extends Function<EmasAgent, Tuple2<EmasAgent, EmasAgent>> {

	/**
	 * Creates a new builder for asexual reproduction.
	 *
	 * @param <S>
	 * 		the solution type
	 */
	static <S extends Solution<?>> AsexualReproductionBuilder<S> builder() {
		return new AsexualReproductionBuilder<>();
	}
}
