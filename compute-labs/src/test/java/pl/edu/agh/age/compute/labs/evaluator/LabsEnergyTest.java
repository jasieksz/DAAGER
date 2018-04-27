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

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.labs.LabsSequences;
import pl.edu.agh.age.compute.labs.solution.LabsSolution;

import org.junit.Test;

public final class LabsEnergyTest {

	@Test public void testLabsLength4Energy() {
		// Given
		final LabsSolution sequence = LabsSequences.length4Sequence();

		// When
		final int energy = new LabsEnergy(sequence).value();

		// Then
		assertThat(energy).isEqualTo(LabsSequences.length4Energy());
	}

	@Test public void testLabsLength5Energy() {
		// Given
		final LabsSolution sequence = LabsSequences.length5Sequence();

		// When
		final int energy = new LabsEnergy(sequence).value();

		// Then
		assertThat(energy).isEqualTo(LabsSequences.length5Energy());
	}

	@Test public void testLabsLength6Energy() {
		// Given
		final LabsSolution sequence = LabsSequences.length6Sequence();

		// When
		final int energy = new LabsEnergy(sequence).value();

		// Then
		assertThat(energy).isEqualTo(LabsSequences.length6Energy());
	}

	@Test public void testLabsLength7Energy() {
		// Given
		final LabsSolution sequence = LabsSequences.length7Sequence();

		// When
		final int energy = new LabsEnergy(sequence).value();

		// Then
		assertThat(energy).isEqualTo(LabsSequences.length7Energy());
	}

}
