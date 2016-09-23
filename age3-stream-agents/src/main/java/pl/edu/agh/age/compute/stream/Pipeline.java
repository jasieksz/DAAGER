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

package pl.edu.agh.age.compute.stream;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

public class Pipeline<T, P extends Pipeline<T, P>> {

	protected final List<T> population;

	protected final Function<List<T>, P> pipelineFactory;

	protected Pipeline(final List<T> population, final Function<List<T>, P> pipelineFactory) {
		this.population = requireNonNull(population);
		this.pipelineFactory = requireNonNull(pipelineFactory);
	}

	public Tuple2<P, P> split(final Predicate<? super T> predicate) {
		final Tuple2<List<T>, List<T>> tuple = population.partition(predicate);
		return Tuple.of(pipelineFactory.apply(tuple._1), pipelineFactory.apply(tuple._2));
	}

	/**
	 * Extracts the current population from the pipeline.
	 *
	 * @return a current population.
	 */
	public List<T> extract() {
		return population;
	}
}