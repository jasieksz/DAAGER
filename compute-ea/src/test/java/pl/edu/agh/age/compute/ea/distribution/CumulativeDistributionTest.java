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


package pl.edu.agh.age.compute.ea.distribution;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public final class CumulativeDistributionTest {

	@Test public void behavesProperlyOnSingleData() {
		// given
		final double[] data = {1.0};
		final CumulativeDistribution distribution = new CumulativeDistribution(data);

		// then
		assertThat(distribution.getValueFor(0.0)).isEqualTo(0);
		assertThat(distribution.getValueFor(0.5)).isEqualTo(0);
		assertThat(distribution.getValueFor(1.0)).isEqualTo(0);
	}

	@Test public void behavesProperlyOnMultipleData() {
		// given
		final double[] data = {0.0, 0.5, 1.0};
		final CumulativeDistribution distribution = new CumulativeDistribution(data);

		// then
		assertThat(distribution.getValueFor(0.0)).isEqualTo(0);
		assertThat(distribution.getValueFor(0.25)).isEqualTo(1);
		assertThat(distribution.getValueFor(0.50)).isEqualTo(1);
		assertThat(distribution.getValueFor(0.75)).isEqualTo(2);
		assertThat(distribution.getValueFor(1.0)).isEqualTo(2);
	}

	@Test public void behavesProperlyWhenDataDoesNotStartOnZero() {
		// given
		final double[] data = {0.5, 1.0};
		final CumulativeDistribution distribution = new CumulativeDistribution(data);

		// then
		assertThat(distribution.getValueFor(0.0)).isEqualTo(0);
		assertThat(distribution.getValueFor(0.25)).isEqualTo(0);
		assertThat(distribution.getValueFor(0.5)).isEqualTo(0);
		assertThat(distribution.getValueFor(0.75)).isEqualTo(1);
		assertThat(distribution.getValueFor(1.0)).isEqualTo(1);
	}
}
