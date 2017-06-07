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


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.function.Predicate;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

/*
 * Created: 2016-11-09.
 */
public final class PipelineTest {

	@Test public void testSplit() {
		final IntPipeline pipeline = new IntPipeline(Stream.range(-10, 10).toList());
		final Predicate<Integer> p = x -> x < 0;

		final Tuple2<IntPipeline, IntPipeline> split = pipeline.split(p);

		assertThat(split._1.extract()).allMatch(p);
		assertThat(split._2.extract()).allMatch(p.negate());
	}

	@Test public void testMergeWith() {
		final IntPipeline pipeline1 = new IntPipeline(Stream.range(0, 10).toList());
		final IntPipeline pipeline2 = new IntPipeline(Stream.range(10, 20).toList());

		final IntPipeline merged = pipeline1.mergeWith(pipeline2);

		assertThat(merged.extract()).containsExactlyElementsOf(Stream.range(0, 20));
	}

	@Test public void testMergeWith_withRepetitions() {
		final IntPipeline pipeline1 = new IntPipeline(Stream.range(0, 10).toList());
		final IntPipeline pipeline2 = new IntPipeline(Stream.range(5, 15).toList());

		final IntPipeline merged = pipeline1.mergeWith(pipeline2);

		assertThat(merged.extract()).containsExactlyElementsOf(Stream.range(0, 15));
	}


	private static class IntPipeline extends Pipeline<Integer, IntPipeline> {
		public IntPipeline(final List<Integer> integers) {
			super(integers, IntPipeline::new);
		}
	}
}
