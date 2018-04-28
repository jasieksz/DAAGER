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

package pl.edu.agh.age.compute.ea.rand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CauchyGeneratorTest {

	@Mock private NormalizedDoubleRandomGenerator rand;

	@InjectMocks private CauchyGenerator underTest;

	@Test public void testBounds() {
		assertThat(underTest.lowerDouble()).isEqualTo(Double.MIN_VALUE);
		assertThat(underTest.upperDouble()).isEqualTo(Double.MAX_VALUE);
	}

	@Test public void testNextDouble() {
		// given
		given(rand.nextDouble()).willReturn(0.0, 0.5, 1.0);

		// then
		assertThat(underTest.nextDouble()).isEqualTo(Math.tan(Math.PI * -0.5));
		assertThat(underTest.nextDouble()).isEqualTo(Math.tan(0.0));
		assertThat(underTest.nextDouble()).isEqualTo(Math.tan(Math.PI * 0.5));
	}
}
