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

package pl.edu.agh.age.compute.stream.emas;

import java.util.Random;
import java.util.function.BiFunction;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.List;

public final class Selectors {
	private Selectors() {}

	public static <T> BiFunction<T, List<T>, Tuple2<T, T>> random() {
		final Random random = new Random();
		return (emasAgent, listOfAgents) -> Tuple.of(emasAgent, listOfAgents.get(random.nextInt(listOfAgents.size())));
	}
}
