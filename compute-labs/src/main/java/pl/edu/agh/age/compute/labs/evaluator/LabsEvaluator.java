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

package pl.edu.agh.age.compute.labs.evaluator;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.labs.solution.LabsSolution;
import pl.edu.agh.age.compute.stream.problem.Evaluator;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;

public class LabsEvaluator implements Evaluator<LabsSolution> {

	protected final EvaluatorCounter counter;

	public LabsEvaluator(final EvaluatorCounter counter) {
		this.counter = requireNonNull(counter);
	}

	@Override public double evaluate(final LabsSolution sequence) {
		final double length = sequence.length();
		final double energy = new LabsEnergy(sequence).value();
		counter.increment();
		return meritFactorOf(length, energy);
	}

	public static double meritFactorOf(final double length, final double labsEnergy) {
		return length * length / (2.0 * labsEnergy); // merit factor F
	}

}
