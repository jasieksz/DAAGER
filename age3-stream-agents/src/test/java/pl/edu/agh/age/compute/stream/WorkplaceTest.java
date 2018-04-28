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

package pl.edu.agh.age.compute.stream;

import pl.edu.agh.age.compute.stream.emas.EmasAgent;
import pl.edu.agh.age.compute.stream.emas.Generators;
import pl.edu.agh.age.compute.stream.emas.StatisticsKeys;
import pl.edu.agh.age.compute.stream.example.SampleAfterStepAction;
import pl.edu.agh.age.compute.stream.example.SampleStep;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public final class WorkplaceTest {

	@Mock private Manager manager;

	@Before public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test public void testPipeline() throws InterruptedException {
		final Step<EmasAgent> step = new SampleStep();
		final AfterStepAction<EmasAgent, StatisticsKeys> afterStepAction = new SampleAfterStepAction();
		final Workplace<EmasAgent> workplace = new Workplace<>(0, Generators.randomAgents(10),
		                                                       BeforeStepAction.simpleMerge(), step, afterStepAction,
		                                                       manager);

		final Thread thread = new Thread(workplace);
		thread.start();
		TimeUnit.SECONDS.sleep(5);
		thread.interrupt();
	}
}
