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

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import pl.edu.agh.age.compute.api.DistributionUtilities;
import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.compute.api.UnicastMessenger;
import pl.edu.agh.age.compute.api.WorkerAddress;
import pl.edu.agh.age.compute.stream.configuration.Configuration;
import pl.edu.agh.age.compute.stream.configuration.WorkplaceConfiguration;

import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import javaslang.collection.HashMap;
import javaslang.collection.Map;

/**
 * Main runtime for stream-like agents processing.
 */
public final class StreamAgents implements Runnable, Manager {

	private static final Logger logger = LoggerFactory.getLogger(StreamAgents.class);

	private final ThreadPool threadPool;

	private final DistributionUtilities distributionUtilities;

	private final IdGenerator workplaceIdGenerator;

	private final UnicastMessenger messenger;

	private final IMap<Long, Map<Object, Object>> statistics;

	private final IMap<Long, WorkerAddress> workplacesLocations;

	private final List<Workplace<Agent>> localWorkplaces;

	private final StopCondition stopCondition;

	@Inject
	public StreamAgents(final Configuration configuration, final ThreadPool threadPool,
	                    final DistributionUtilities distributionUtilities, final UnicastMessenger messenger) {
		requireNonNull(configuration);
		this.threadPool = requireNonNull(threadPool);
		this.distributionUtilities = requireNonNull(distributionUtilities);
		this.messenger = requireNonNull(messenger);

		workplaceIdGenerator = this.distributionUtilities.getIdGenerator("workplace");
		statistics = this.distributionUtilities.getMap("statistics");
		workplacesLocations = this.distributionUtilities.getMap("workplace-locations");

		// Process configuration
		stopCondition = configuration.stopCondition();
		final List<WorkplaceConfiguration<Agent>> workplaceConfigurations = configuration.workplaces();
		localWorkplaces = workplaceConfigurations.stream()
		                                         .map(c -> c.toWorkplace(workplaceIdGenerator.newId(), this))
		                                         .collect(toList());

		// Update location information
		localWorkplaces.stream().map(Workplace::id).forEach(id -> workplacesLocations.put(id, messenger.address()));

		this.messenger.<MigrationMessage>registerListener((message, sender) -> {
			assert message instanceof MigrationMessage;

			localWorkplaces.stream()
			               .filter(workplace -> workplace.id() == message.targetWorkplace)
			               .findAny()
			               .ifPresent(workplace -> workplace.addAgentInNextStep(message.agent));
		});
	}

	@Override public void run() {
		logger.info("Stream agents starting");

		// Submit workplaces and wait for finalization
		final List<ListenableFuture<?>> workplaceFutures = threadPool.submitAll(localWorkplaces);
		try {
			waitFor(stopCondition);
		} catch (final InterruptedException ignored) {
			logger.info("Processing interrupted");
			Thread.currentThread().interrupt();
		}

		// Cancel all workplaces and wait for them. They do not return any
		// values so we just want to be sure that they are finished.
		workplaceFutures.forEach(f -> f.cancel(true));
		workplaceFutures.forEach(f -> {
			try {
				f.get();
			} catch (final CancellationException ignored) {
				logger.debug("Task cancelled (probably by us)");
			} catch (final InterruptedException ignored) {
				logger.debug("Waiting for the workplace was interrupted");
			} catch (final ExecutionException e) {
				logger.error("Computation threw an exception that was not caught. Possible bug in core?", e);
			}
		});

		logger.info("Stream agents finished");
	}

	private static void waitFor(final StopCondition stopCondition) throws InterruptedException {
		while (true) {
			if (stopCondition.isReached()) {
				logger.info("Stop condition reached");
				return;
			}
			TimeUnit.SECONDS.sleep(1);
		}
	}

	// Manager methods

	@Override public void postStatistics(final long id, final Map<Object, Object> workplaceStatistics) {
		statistics.put(id, workplaceStatistics);
	}

	@Override public Map<Long, Map<Object, Object>> getStatistics() {
		return HashMap.ofAll(statistics);
	}

	@Override public void migrate(final Agent agent, final long targetWorkplace) {
		requireNonNull(agent);
		checkArgument(targetWorkplace >= 0);

		final WorkerAddress targetAddress = workplacesLocations.get(targetWorkplace);
		logger.debug("Address for Workplace {} is {}", targetWorkplace, targetAddress);
		if (targetAddress == null) {
			throw new IllegalArgumentException("Unknown workplace"); // XXX: Better exception
		}

		logger.debug("Sending {} to {}", agent, targetAddress);
		messenger.send(targetAddress, new MigrationMessage(targetWorkplace, agent));
	}

	@Override public boolean isStopConditionReached() {
		return stopCondition.isReached();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
