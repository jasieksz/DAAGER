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

import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;
import pl.edu.agh.age.compute.ea.solution.DoubleVectorSolution;

/**
 * Decodes a binary solution to a real-valued one.
 */
public class BinaryToRvDecoder implements SolutionDecoder<BooleanVectorSolution, DoubleVectorSolution> {

	private static final long serialVersionUID = 8538572448279996042L;

	@Override public final DoubleVectorSolution decodeSolution(final BooleanVectorSolution solution) {
		return new DoubleVectorSolution(transformRepresentation(solution.valuesAsPrimitive()));
	}

	protected double binaryToDouble(final boolean[] representation, final int offset, final int length) {
		final long longBits = binaryToLongBits(representation, offset, length);
		return Double.longBitsToDouble(longBits);
	}

	private double[] transformRepresentation(final boolean[] representation) {
		final int n = representation.length / Double.SIZE;
		final double[] decoded = new double[n];
		for (int i = 0; i < n; i++) {
			decoded[i] = binaryToDouble(representation, i * Double.SIZE, Double.SIZE);
		}
		return decoded;
	}

	private static long binaryToLongBits(final boolean[] representation, final int offset, final int length) {
		long longBits = 0;
		for (int i = offset; i < (offset + length); i++) {
			longBits <<= 1;
			longBits += representation[i] ? 1 : 0;
		}
		return longBits;
	}
}
