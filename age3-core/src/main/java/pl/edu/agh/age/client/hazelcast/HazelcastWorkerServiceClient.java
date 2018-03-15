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

package pl.edu.agh.age.client.hazelcast;

import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.worker.WorkerMessage;
import pl.edu.agh.age.services.worker.internal.HazelcastObjectNames;
import pl.edu.agh.age.services.worker.internal.ComputationState;
import pl.edu.agh.age.services.worker.internal.configuration.WorkerConfiguration;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastWorkerServiceClient implements WorkerServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(HazelcastWorkerServiceClient.class);

	private static final ImmutableSet<ComputationState> NOT_FINISHED_STATES = ImmutableSet.copyOf(
		EnumSet.of(ComputationState.RUNNING, ComputationState.CONFIGURED));

	private final ITopic<WorkerMessage<?>> workerTopic;

	private final Map<HazelcastObjectNames.ConfigurationKey, Object> workerConfigurationMap;

	@Inject public HazelcastWorkerServiceClient(final HazelcastInstance hazelcastInstance) {
		workerTopic = hazelcastInstance.getTopic(HazelcastObjectNames.CHANNEL_NAME);
		workerConfigurationMap = hazelcastInstance.getMap(HazelcastObjectNames.CONFIGURATION_MAP_NAME);
	}

	@Override public void startComputation() {
		logger.debug("Starting computation");
		workerTopic.publish(WorkerMessage.createBroadcastWithoutPayload(WorkerMessage.Type.START_COMPUTATION));
	}

	@Override public void stopComputation() {
		logger.debug("Stopping computation");
		workerTopic.publish(WorkerMessage.createBroadcastWithoutPayload(WorkerMessage.Type.CANCEL_COMPUTATION));
	}

	@Override public void cleanConfiguration() {
		logger.debug("Cleaning configuration");
		workerTopic.publish(WorkerMessage.createBroadcastWithoutPayload(WorkerMessage.Type.CLEAN_CONFIGURATION));
	}

	@Override public void prepareConfiguration(final WorkerConfiguration configuration) throws InterruptedException {
		logger.debug("Preparing configuration");
		workerConfigurationMap.put(HazelcastObjectNames.ConfigurationKey.CONFIGURATION, configuration);
		TimeUnit.SECONDS.sleep(2L);
		workerTopic.publish(WorkerMessage.createBroadcastWithoutPayload(WorkerMessage.Type.LOAD_CONFIGURATION));
	}

	@Override public boolean isComputationRunning() {
		return computationState() == ComputationState.RUNNING;
	}

	@Override public boolean isComputationFinished() {
		return !NOT_FINISHED_STATES.contains(computationState());
	}

	@Override public boolean isComputationFailed() {
		return computationState() == ComputationState.FAILED;
	}

	@Override public ComputationState computationState() {
		return (ComputationState)configurationValue(HazelcastObjectNames.ConfigurationKey.COMPUTATION_STATE).orElseGet(
			() -> ComputationState.NONE);
	}

	@Override public Optional<WorkerConfiguration> currentConfiguration() {
		return configurationValue(HazelcastObjectNames.ConfigurationKey.CONFIGURATION);
	}

	@Override public Optional<Throwable> currentError() {
		return configurationValue(HazelcastObjectNames.ConfigurationKey.ERROR);
	}

	@Override public void waitForComputationEnd() throws InterruptedException {
		logger.debug("Waiting for computation to end or fail");
		while (!isComputationFinished()) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
		logger.debug("Computation finished");
	}

	private <T> Optional<T> configurationValue(final HazelcastObjectNames.ConfigurationKey key) {
		return Optional.ofNullable((T)workerConfigurationMap.get(key));
	}
}
