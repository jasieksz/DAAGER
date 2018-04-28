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

package pl.edu.agh.age.compute.stream.emas;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

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
