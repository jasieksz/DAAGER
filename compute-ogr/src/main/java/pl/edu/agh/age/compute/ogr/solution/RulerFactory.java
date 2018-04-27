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

package pl.edu.agh.age.compute.ogr.solution;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ogr.evaluator.RulerEvaluator;
import pl.edu.agh.age.compute.ogr.problem.RulerProblem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@FunctionalInterface
public interface RulerFactory {

	Ruler create();

	static RulerFactory random(final RulerProblem problemDefinition, final RulerEvaluator evaluator) {
		requireNonNull(problemDefinition);
		requireNonNull(evaluator);

		return () -> createRuler(evaluator, problemDefinition.marksCount(), problemDefinition.maxAllowedDistance());
	}

	static Ruler createRuler(final RulerEvaluator evaluator, final int marksCount, final int maxMarkValue) {
		// This method generates INDIRECT representation. INDIRECT representation is shorter than DIRECT by one.
		final List<Integer> allPossibleMarks = generatePossibleDistances(maxMarkValue);
		Collections.shuffle(allPossibleMarks);

		// In the next step we add mark '1' so we now must select n - 2 distances
		final List<Integer> possibleMarks = allPossibleMarks.subList(0, marksCount - 2);
		final int distanceOnePosition = ThreadLocalRandom.current().nextInt(possibleMarks.size());
		possibleMarks.add(distanceOnePosition, 1);

		final Ruler ruler = new Ruler(possibleMarks, false);
		return ruler.withFitness(evaluator.evaluate(ruler));
	}

	static List<Integer> generatePossibleDistances(final int maxValue) {
		final List<Integer> marks = new ArrayList<>(maxValue - 1);
		for (int i = 2; i <= maxValue; i++) {
			// distance '0' is illegal, distance '1' will be in ALL rulers, so we need to permutate only n - 1 numbers
			marks.add(i);
		}
		return marks;
	}

}
