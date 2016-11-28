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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public final class Predicates {
	private Predicates() {}

	public static <T> Predicate<T> random(final double probability) {
		final Random random = ThreadLocalRandom.current();
		return x -> random.nextDouble() < probability;
	}

	public static Predicate<EmasAgent> energyAboveThreshold(final double energyThreshold) {
		return agent -> agent.energy >= energyThreshold;
	}

	public static Predicate<EmasAgent> energyBelowThreshold(final double energyThreshold) {
		return agent -> agent.energy <= energyThreshold;
	}
}
