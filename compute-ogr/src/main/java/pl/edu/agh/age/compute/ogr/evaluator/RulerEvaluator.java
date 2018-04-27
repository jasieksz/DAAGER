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

package pl.edu.agh.age.compute.ogr.evaluator;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ogr.RulerUtils;
import pl.edu.agh.age.compute.ogr.solution.Ruler;
import pl.edu.agh.age.compute.stream.problem.Evaluator;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

public final class RulerEvaluator implements Evaluator<Ruler> {

	public static final double VIOLATION_PENALTY = 375;

	private final EvaluatorCounter counter;

	public RulerEvaluator(final EvaluatorCounter counter) {
		this.counter = requireNonNull(counter);
	}

	@Override public double evaluate(final Ruler ruler) {
		// Reference 1 -
		// https://gitlab.com/age-agh/kaczmarczyk-ogr/blob/master/src/main/java/ogr/ogr/emas/OgrSolutionEvaluator.java
		// Reference 2 -
		// https://gitlab.com/age-agh/age-2.7-ogr-labs-scala/blob/luke-emas/src/main/scala/pl/edu/agh/ogr/solution/RulerEvaluator.scala
		final int violations = RulerUtils.calculateViolations(ruler);
		counter.increment();
		return evaluate(ruler.length(), violations);
	}

	public static double evaluate(final int rulerLength, final int violationsCount) {
		return rulerLength + (violationsCount * VIOLATION_PENALTY);
	}

}
