package pl.edu.agh.age.compute.stream.emas;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.emas.solution.Solution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;

import javaslang.collection.Seq;

/**
 * PopulationEvaluator is a function that computes fitness and optionally improves all of the
 * individuals in a given population.
 *
 * @param <A>
 *        the type an individual agent
 */
@FunctionalInterface
public interface PopulationEvaluator<A extends Agent> {

	Seq<EmasAgent> evaluate(Seq<EmasAgent> population);

	@SuppressWarnings("unchecked")
	static <S extends Solution<?>> PopulationEvaluator<EmasAgent> simpleEvaluator(final Evaluator<S> evaluator) {
		requireNonNull(evaluator, "Evaluator has not been defined");
		return (population) -> {
			return population.map(agent -> {
				final S solution = (S)agent.solution;
				solution.updateFitness(evaluator.evaluate(solution));
				return EmasAgent.create(agent.energy, solution);
			});
		};
	}

}
