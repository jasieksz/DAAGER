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

package pl.edu.agh.age.compute.ogr.recombination;

import pl.edu.agh.age.compute.ogr.RulerUtils;
import pl.edu.agh.age.compute.ogr.solution.Ruler;
import pl.edu.agh.age.compute.stream.emas.reproduction.recombination.Recombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SimpleRulerRecombination implements Recombination<Ruler> {

	/**
	 * THIS CLASS DOES NOT PERFORM RECOMBINATION AS IT IS PERCEIVED BY A COMPUTATIONAL INTELLIGENCE!
	 * A recombination should take part of a first solution, part of a second solution and join them together, creating
	 * a new result solution. This temporary implementation does not guarantee that result solution will always
	 * originate from more than one input solution. Moreover, input solutions' parts are shuffled during the
	 * recombination, which is against recombination rules and makes this process look more like a mutation than
	 * a recombination.
	 */

	// TODO - Change to a proper recombination implementation somewhere in the undefined future...
	@Override public Ruler recombine(final Ruler firstSolution, final Ruler secondSolution) {

		// 1. Take all measurable distances from the first solution.
		// 2. Take all measurable distances from the second solution.
		// 3. Shuffle obtained distances.
		// 4. Randomly choose distances for the child ruler.

		final int length = firstSolution.marksCount();

		final Set<Integer> measurableDistances = new HashSet<>();
		measurableDistances.addAll(RulerUtils.getMeasurableDistances(firstSolution, false));
		measurableDistances.addAll(RulerUtils.getMeasurableDistances(secondSolution, false));

		final List<Integer> shuffledDistances = new ArrayList<>(measurableDistances);
		Collections.shuffle(shuffledDistances);

		final List<Integer> childDistances = shuffledDistances.subList(0, length - 2);
		childDistances.add(1);

		return new Ruler(childDistances, false);
	}

}
