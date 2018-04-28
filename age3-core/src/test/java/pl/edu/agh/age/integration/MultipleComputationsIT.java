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

package pl.edu.agh.age.integration;

import static com.google.common.collect.Lists.newCopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.worker.TaskStartedEvent;
import pl.edu.agh.age.services.worker.WorkerServiceEvent;
import pl.edu.agh.age.services.worker.internal.ComputationState;
import pl.edu.agh.age.services.worker.internal.configuration.SpringConfiguration;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring-test-node.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public final class MultipleComputationsIT {

	@Inject private EventBus eventBus;

	@Inject private WorkerServiceClient workerServiceClient;

	@Inject private ResourceLoader resourceLoader;

	private final List<WorkerServiceEvent> events = newCopyOnWriteArrayList();

	@Before public void setUp() {
		eventBus.register(this);
	}

	@Test public void testMultipleComputations() throws IOException, InterruptedException {
		for (int i = 0; i < 2; i++) {
			// Configure
			final SpringConfiguration configuration = new SpringConfiguration(
				resourceLoader.getResource("classpath:compute/spring-simple-test.xml"), new Properties());
			workerServiceClient.prepareConfiguration(configuration);
			TimeUnit.SECONDS.sleep(3L);
			assertThat(workerServiceClient.computationState()).isEqualTo(ComputationState.CONFIGURED);

			// Start
			workerServiceClient.startComputation();
			workerServiceClient.waitForComputationEnd();
			assertThat(workerServiceClient.computationState()).isEqualTo(ComputationState.FINISHED);
			assertThat(events).hasAtLeastOneElementOfType(TaskStartedEvent.class);

			// Clean
			workerServiceClient.cleanConfiguration();
			TimeUnit.SECONDS.sleep(3L);
			assertThat(workerServiceClient.computationState()).isEqualTo(ComputationState.NONE);
		}
	}

	@Subscribe public void listenForEvents(final WorkerServiceEvent event) {
		events.add(event);
	}
}
