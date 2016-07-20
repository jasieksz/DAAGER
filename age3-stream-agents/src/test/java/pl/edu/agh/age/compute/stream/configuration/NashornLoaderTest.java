/*
 * Copyright (C) 2016 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.stream.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.StopCondition;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public final class NashornLoaderTest {

	@Test public void testLoading() {
		final JUnitSoftAssertions softly = new JUnitSoftAssertions();

		final InputStream inputStream = getClass().getResourceAsStream("/emas-config.js");
		final Configuration configuration = NashornLoader.load(inputStream);

		assertThat(configuration).isNotNull();

		final StopCondition stopCondition = configuration.stopCondition();
		final List<WorkplaceConfiguration<Agent>> workplaces = configuration.workplaces();

		softly.assertThat(stopCondition).isNotNull();
		softly.assertThat(workplaces).isNotNull().hasSize(2);

		softly.assertThat(workplaces).extracting("agents").hasSize(5);
		softly.assertThat(workplaces).extracting("step").isNotNull();
		softly.assertThat(workplaces).extracting("afterStep").isNotNull();
	}
}
