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

import pl.edu.agh.age.compute.stream.emas.solution.Solutions;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.vavr.collection.List;
import io.vavr.collection.Stream;

/**
 * Generators for agents
 */
public final class Generators {
	private Generators() {}

	public static List<EmasAgent> randomAgents(final int count) {
		final Random random = ThreadLocalRandom.current();
		// TODO: solution
		return Stream.range(0, count)
		             .map(i -> EmasAgent.create(random.nextDouble(), Solutions.singleDouble(1.0)))
		             .toList();
	}

	public static EmasAgent randomAgent() {
		final Random random = ThreadLocalRandom.current();
		// TODO: solution
		return EmasAgent.create(random.nextDouble(), Solutions.singleDouble(1.0));
	}
}
