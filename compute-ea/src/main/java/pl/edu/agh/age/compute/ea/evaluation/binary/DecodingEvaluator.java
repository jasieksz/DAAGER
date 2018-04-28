/*
 * Copyright (C) 2006-2018 Intelligent Information Systems Group.
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


package pl.edu.agh.age.compute.ea.evaluation.binary;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.evaluation.Evaluator;
import pl.edu.agh.age.compute.ea.solution.Solution;

/**
 * An evaluator implementation which applies a {@link SolutionDecoder} before delegating to some other
 * {@link SolutionEvaluator}, effectively acting as a bridge.
 *
 * @param <S>
 * 		The decoder source solution type
 * @param <T>
 * 		The decoder target solution type
 */
public final class DecodingEvaluator<S extends Solution<?>, T extends Solution<?>> implements Evaluator<S> {

	private final SolutionDecoder<S, T> solutionDecoder;

	private final Evaluator<T> solutionEvaluator;

	public DecodingEvaluator(final SolutionDecoder<S, T> solutionDecoder, final Evaluator<T> solutionEvaluator) {
		this.solutionDecoder = requireNonNull(solutionDecoder);
		this.solutionEvaluator = requireNonNull(solutionEvaluator);
	}

	@Override public double evaluate(final S solution) {
		final T decodedSolution = solutionDecoder.decodeSolution(solution);
		return solutionEvaluator.evaluate(decodedSolution);
	}
}
