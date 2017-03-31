package pl.edu.agh.age.compute.stream.emas;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Seq;

public final class PipelineUtils {

	private PipelineUtils() {}

	/**
	 * Extracts a tuple containing two pipelines from a given list of agent tuples.
	 *
	 * @param agentTuples
	 * 		the agent tuples list
	 * @return the tuple containing two pipelines (usually first is a pipeline of parents and a second a pipeline of children)
	 */
	public static Tuple2<Pipeline, Pipeline> flattenToPipelineTuple(final List<Tuple2<Seq<EmasAgent>, EmasAgent>> agentTuples) {
		final List<EmasAgent> parents = agentTuples.flatMap(tuple -> tuple._1()) //
		                                           .filter(agent -> agent != null) //
		                                           .distinct();
		final List<EmasAgent> children = agentTuples.map(tuple -> tuple._2()) //
		                                            .filter(agent -> agent != null) //
		                                            .distinct();
		return Tuple.of(new Pipeline(parents), new Pipeline(children));
	}

	/**
	 * Extracts a tuple containing two pipelines from a given list of agent tuples.
	 *
	 * @param agentTuples
	 * 		the agent tuples list
	 * @return the tuple containing two pipelines (usually first is a pipeline of parents and a second a pipeline of children)
	 */
	public static Tuple2<Pipeline, Pipeline> extractPipelineTuple(final List<Tuple2<EmasAgent, EmasAgent>> agentTuples) {
		final List<EmasAgent> parents = agentTuples.map(tuple -> tuple._1()) //
		                                           .filter(agent -> agent != null) //
		                                           .distinct();
		final List<EmasAgent> children = agentTuples.map(tuple -> tuple._2()) //
		                                            .filter(agent -> agent != null) //
		                                            .distinct();
		return Tuple.of(new Pipeline(parents), new Pipeline(children));
	}

}
