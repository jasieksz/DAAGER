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

package pl.edu.agh.age.compute.stream;

import static org.mockito.Mockito.mock;

import pl.edu.agh.age.compute.stream.configuration.Configuration;
import pl.edu.agh.age.compute.stream.configuration.WorkplaceConfiguration;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import javax.inject.Inject;

import javaslang.collection.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring-stream-static.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public final class SpringConfigurationIT {

	@Inject private Configuration configuration;

	private final Environment environment = new Environment(0, mock(Manager.class));

	@Test public void configurationLoadsAndWorks() {
		final JUnitSoftAssertions softly = new JUnitSoftAssertions();

		final StopCondition stopCondition = configuration.stopCondition();

		softly.assertThat(stopCondition).isNotNull().isExactlyInstanceOf(TimedStopCondition.class);

		final List<WorkplaceConfiguration<Agent>> workplaces = configuration.workplaces();

		softly.assertThat(workplaces).isNotNull().hasSize(1);
		softly.assertThat(workplaces).extracting("agents").isNotNull().hasSize(5);
		softly.assertThat(workplaces).extracting("step").isNotNull();
		softly.assertThat(workplaces).extracting("afterStep").isNotNull();

		final WorkplaceConfiguration<Agent> workplaceConfiguration = workplaces.get(0);
		final Step<Agent> step = workplaceConfiguration.step();
		final AfterStepAction<Agent, ?> afterStepAction = workplaceConfiguration.afterStep();
		final javaslang.collection.List<Agent> agents = javaslang.collection.List.ofAll(
			workplaceConfiguration.agents());

		final javaslang.collection.List<Agent> agentsAfterStep = step.stepOn(agents, environment);

		softly.assertThat(agentsAfterStep).isNotEmpty();

		final Map<?, Object> stats = afterStepAction.apply(agentsAfterStep);

		softly.assertThat(stats).isNotEmpty();
	}
}
