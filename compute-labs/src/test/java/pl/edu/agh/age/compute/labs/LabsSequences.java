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

package pl.edu.agh.age.compute.labs;

import pl.edu.agh.age.compute.labs.evaluator.LabsFastFlipEvaluator;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vavr.Tuple4;

public final class LabsSequences {

	private LabsSequences() {}

	/* L = 4 */

	public static LabsSolution length4Sequence() {
		return new LabsSolution(new boolean[] {true, true, true, false});
	}

	public static int length4Energy() {
		return 2;
	}

	public static double length4MeritFactor() {
		return 4.0;
	}

	/* L = 5 */

	public static LabsSolution length5Sequence() {
		return new LabsSolution(new boolean[] {false, false, false, true, false});
	}

	public static int length5Energy() {
		return 2;
	}

	public static double length5MeritFactor() {
		return 6.25;
	}

	/* L = 6 */

	public static LabsSolution length6Sequence() {
		return new LabsSolution(new boolean[] {true, true, true, true, false, true});
	}

	public static int length6Energy() {
		return 7;
	}

	public static double length6MeritFactor() {
		return 2.5714;
	}

	/* L = 7 */

	public static LabsSolution length7Sequence() {
		return new LabsSolution(new boolean[] {false, false, false, true, true, false, true});
	}

	public static int length7Energy() {
		return 3;
	}

	public static double length7MeritFactor() {
		return 8.1666;
	}

	/*
	 * best known values from: https://raw.githubusercontent.com/borkob/git_labs/master/results-2018/2016-labs-skew.txt
	 */

	public static int[] bests() {
		return new int[] {};
	}

	public static LabsSolution best(final int length) {
		if (bests.containsKey(length)) {
			return bests.get(length)._4();
		}
		return null;
	}

	public static int bestEnergy(final int length) {
		if (bests.containsKey(length)) {
			return bests.get(length)._3();
		}
		return -1;
	}

	public static double bestMeritFactor(final int length) {
		if (bests.containsKey(length)) {
			return bests.get(length)._2();
		}
		return -1.0;
	}

	private static final Map<Integer, Tuple4<Integer, Double, Integer, LabsSolution>> bests;

	static {
		try {
			final List<String> lines = Files.readAllLines(
				Paths.get(LabsSequences.class.getResource("2016-labs-skew.txt").toURI()));

			bests = lines.stream()
			             .map(line -> line.split("\\t"))
			             .filter(elements -> (elements.length == 4) && elements[0].matches("\\d+"))
			             .map(elements -> new Tuple4<>(Integer.valueOf(elements[0]), Double.valueOf(elements[1]),
			                                           Integer.valueOf(elements[2]), convertSequence(elements[3])))
			             .collect(Collectors.toMap(Tuple4::_1, x -> x));

		} catch (final IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static final LabsSolution convertSequence(final String sequence) {
		final int sequenceLength = sequence.toCharArray().length;
		final boolean[] boolRepresentation = new boolean[sequenceLength];
		for (int i = 0; i < sequenceLength; i++) {
			boolRepresentation[i] = sequence.toCharArray()[i] != '0';

		}
		return new LabsSolution(boolRepresentation);
	}

	/*
	 * Neighborhood of a sequence
	 */

	public static List<LabsSolution> generateNeighborhoodFor(final LabsSolution solution) {
		final LabsFastFlipEvaluator evaluator = new LabsFastFlipEvaluator(solution);
		final List<LabsSolution> neighborhood = new ArrayList<>(solution.length());
		for (int i = 0; i < solution.length(); i++) {
			final boolean[] sequence = Arrays.copyOf(solution.sequenceRepresentation(), solution.length());
			sequence[i] = !sequence[i];
			neighborhood.add(new LabsSolution(sequence).withFitness(evaluator.evaluateFlipped(i)));
		}
		return neighborhood;
	}


}
