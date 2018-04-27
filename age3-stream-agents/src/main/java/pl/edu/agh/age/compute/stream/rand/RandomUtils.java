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

package pl.edu.agh.age.compute.stream.rand;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RandomUtils {

	private RandomUtils() {}

	/**
	 * Generates a sequence of random numbers with a given parameters.
	 *
	 * @param lowerInclusiveBound
	 *        the lower inclusive bound of a sequence
	 * @param upperInclusiveBound
	 *        the upper inclusive bound of a sequence
	 * @param sequenceLength
	 *        the result sequence length
	 * @return the collection
	 */
	public static Collection<Integer> generateSequence(final int lowerInclusiveBound, final int upperInclusiveBound,
	                                                   final int sequenceLength) {
		return generateSequence(lowerInclusiveBound, upperInclusiveBound, sequenceLength, Collections.emptySet());
	}

	/**
	 * Generates a sequence of random numbers with a given parameters.
	 *
	 * @param lowerInclusiveBound
	 *        the lower inclusive bound of a sequence
	 * @param upperInclusiveBound
	 *        the upper inclusive bound of a sequence
	 * @param sequenceLength
	 *        the result sequence length
	 * @param restrictedValues
	 *        the restricted numbers which cannot appear in the result sequence
	 * @return the collection
	 */
	public static Collection<Integer> generateSequence(final int lowerInclusiveBound, final int upperInclusiveBound,
	                                                   final int sequenceLength,
	                                                   final Collection<Integer> restrictedValues) {
		requireNonNull(restrictedValues);
		checkState(upperInclusiveBound >= lowerInclusiveBound);

		final int rangeSize = upperInclusiveBound + 1 - lowerInclusiveBound;
		final int availableValues = rangeSize - restrictedValues.size();
		checkState(availableValues >= sequenceLength,
		           "Given bounds are too narrow to choose from - maybe there are too many restricted values?");
		final Random rand = ThreadLocalRandom.current();

		final Set<Integer> restricted = restrictedValues.stream().collect(Collectors.toSet());
		final List<Integer> result = new ArrayList<>(sequenceLength);

		for (int i = 0; i < sequenceLength; i++) {
			final int randomNumber = nextInt(rand, lowerInclusiveBound, upperInclusiveBound + 1, restricted);
			restricted.add(randomNumber);
			result.add(randomNumber);
		}

		return result;
	}

	/* Generating random numbers with restricted values */

	public static int nextInt(final int upperExclusiveBound, final int restrictedValue) {
		return nextInt(0, upperExclusiveBound, Arrays.asList(restrictedValue));
	}

	public static int nextInt(final int upperExclusiveBound, final Collection<Integer> restrictedValues) {
		return nextInt(0, upperExclusiveBound, restrictedValues);
	}

	public static int nextInt(final int lowerInclusiveBound, final int upperExclusiveBound, final int restrictedValue) {
		return nextInt(lowerInclusiveBound, upperExclusiveBound, Arrays.asList(restrictedValue));
	}

	public static int nextInt(final int lowerInclusiveBound, final int upperExclusiveBound,
	                          final Collection<Integer> restrictedValues) {
		requireNonNull(restrictedValues);
		checkState(upperExclusiveBound > lowerInclusiveBound);
		checkState(upperExclusiveBound - lowerInclusiveBound > restrictedValues.size(), "Too many restricted numbers");

		final Random rand = ThreadLocalRandom.current();
		final Set<Integer> restricted = new HashSet<>(restrictedValues);
		return nextInt(rand, lowerInclusiveBound, upperExclusiveBound, restricted);
	}

	private static int nextInt(final Random rand, final int lowerInclusiveBound, final int upperExclusiveBound,
	                           final Set<Integer> restricted) {
		final List<Integer> numbers = IntStream.range(lowerInclusiveBound, upperExclusiveBound) //
			                                   .boxed() //
			                                   .filter(number -> !restricted.contains(number)) //
			                                   .collect(Collectors.toList());
		return numbers.get(rand.nextInt(numbers.size()));
	}

}
