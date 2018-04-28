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

/**
 * Extends {@link BinaryToRvDecoder} one with Gray decoding.
 */
public final class GrayToRvDecoder extends BinaryToRvDecoder {

	@Override protected double binaryToDouble(final boolean[] representation, final int offset, final int length) {
		final long longBits = graycodeToLongBits(representation, offset, length);
		return Double.longBitsToDouble(longBits);
	}

	private static long graycodeToLongBits(final boolean[] representation, final int offset, final int length) {
		boolean currentBit = representation[offset];
		long longBits = currentBit ? 1 : 0;

		for (int i = offset + 1; i < (offset + length); i++) {
			currentBit = currentBit ^ representation[i];
			longBits <<= 1;
			longBits += currentBit ? 1 : 0;
		}

		return longBits;
	}
}
