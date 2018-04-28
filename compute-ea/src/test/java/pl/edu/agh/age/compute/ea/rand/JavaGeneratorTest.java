/*
 * Copyright (C) 2006-2018   Intelligent Information Systems Group.
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
/**
 * @author Tomasz Sławek & Marcin Świątek
 */
package pl.edu.agh.age.compute.ea.rand;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class JavaGeneratorTest {

	private static final long seed = 1234567890L;

	private JavaRandomGenerator generator;

	@Before public void setUp() {
		generator = new JavaRandomGenerator(seed);
	}

	@Test public void testBounds() {
		assertThat(generator.lowerDouble()).isEqualTo(0.0);
		assertThat(generator.upperDouble()).isEqualTo(1.0);

		assertThat(generator.lowerInt()).isEqualTo(Integer.MIN_VALUE);
		assertThat(generator.upperInt()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test public void testSeed() {
		// given
		final JavaRandomGenerator generator2 = new JavaRandomGenerator(seed);

		// then
		assertThat(generator.nextInt()).isEqualTo(generator2.nextInt());
		assertThat(generator.nextDouble()).isEqualTo(generator2.nextDouble());
	}

	@Test public void testNextDouble() {
		// given
		final Random rand = new Random(seed);

		// then
		for (int i = 0; i < 10000; i++) {
			assertThat(generator.nextDouble()).isEqualTo(rand.nextDouble());
		}
	}

	@Test public void testNextInt() {
		// given
		final Random rand = new Random(seed);

		// then
		for (int i = 0; i < 10000; i++) {
			assertThat(generator.nextInt()).isEqualTo(rand.nextInt());
		}
	}
}
