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
import pl.edu.agh.age.compute.api.TopologyProvider;
import pl.edu.agh.age.compute.api.UnicastMessenger;
import pl.edu.agh.age.compute.api.WorkerAddress;
import pl.edu.agh.age.compute.stream.configuration.Configuration;
import pl.edu.agh.age.compute.stream.configuration.WorkplaceConfiguration;
import pl.edu.agh.age.compute.stream.logging.LoggingService;

import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import one.util.streamex.EntryStream;

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

	private final LoggingService loggingService;

	private final TopologyProvider<Long> topologyProvider;

	@SuppressWarnings("unchecked")
	@Inject
	public StreamAgents(final Configuration configuration, final ThreadPool threadPool,
	                    final DistributionUtilities distributionUtilities, final UnicastMessenger messenger,
	                    final TopologyProvider<?> topologyProvider) {
		requireNonNull(configuration);
		this.threadPool = requireNonNull(threadPool);
		this.distributionUtilities = requireNonNull(distributionUtilities);
		this.messenger = requireNonNull(messenger);
		this.topologyProvider = (TopologyProvider<Long>)requireNonNull(topologyProvider);

		workplaceIdGenerator = this.distributionUtilities.getIdGenerator("workplace");
		statistics = this.distributionUtilities.getMap("statistics");
		workplacesLocations = this.distributionUtilities.getMap("workplace-locations");

		// Process configuration
		stopCondition = configuration.stopCondition();
		loggingService = configuration.loggingService();
		this.topologyProvider.setTopology(configuration.topology());
		final List<WorkplaceConfiguration<Agent>> workplaceConfigurations = configuration.workplaces();
		localWorkplaces = workplaceConfigurations.stream()
		                                         .map(c -> c.toWorkplace(workplaceIdGenerator.newId(), this))
		                                         .collect(toList());

		// Update location information
		final java.util.Set<Long> localWorkplacesIds = localWorkplaces.stream()
		                                                              .map(Workplace::id)
		                                                              .collect(Collectors.toSet());
		localWorkplacesIds.forEach(id -> workplacesLocations.put(id, messenger.address()));
		this.topologyProvider.addNodes(localWorkplacesIds);

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
		loggingService.schedule(this, threadPool);

		// Submit workplaces and wait for finalization
		final List<ListenableFuture<?>> workplaceFutures = threadPool.submitAll(localWorkplaces);
		try {
			waitFor(stopCondition);
		} catch (final InterruptedException ignored) {
			logger.info("Processing interrupted");
			Thread.currentThread().interrupt();
		}

		loggingService.stop();

		workplaceFutures.forEach(StreamAgents::cancelWorkplaceFuture);
		logger.info("Stream agents finished");
	}

	private static void cancelWorkplaceFuture(final ListenableFuture<?> f) {
		// Cancel all workplaces and wait for them. They do not return any
		// values so we just want to be sure that they are finished.
		try {
			logger.debug("Waiting for workplace to finish");
			boolean finished = false;
			// Wait for 10 seconds (counting)
			for (int i = 1; !finished || (i <= 10); i++) {
				try {
					final Object o = f.get(1, TimeUnit.SECONDS);
					logger.debug("OUT {}", o);
					finished = true;
				} catch (final TimeoutException ignored) {
					logger.debug("Still waiting ({} from 10)", i);
				}
			}
			if (!finished) {
				logger.debug("Cancel forced");
				f.cancel(true);
			}
		} catch (final CancellationException ignored) {
			logger.debug("Task cancelled (probably by us)");
		} catch (final InterruptedException ignored) {
			logger.debug("Waiting for the workplace was interrupted");
		} catch (final ExecutionException e) {
			logger.error("Computation threw an exception that was not caught. Possible bug in core?", e);
		}
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

	@Override public int getTotalWorkplacesCount() {
		return statistics.size();
	}

	@Override public Map<Long, Map<Object, Object>> getStatistics() {
		return HashMap.ofAll(statistics);
	}

	@Override public Map<Long, Map<Object, Object>> getLocalStatistics() {
		final Set<Long> localWorkplaceIds = HashSet.ofAll(localWorkplaces.stream().map(workplace -> workplace.id()));
		return HashMap.ofAll(statistics).filterKeys(localWorkplaceIds::contains);
	}

	@Override public Map<Long, Map<Object, Object>> getNeighboursStatistics(final long workplaceId) {
		final Map<Long, Set<String>> neighbours = getNeighboursOf(workplaceId);
		return HashMap.ofAll(statistics).filterKeys(neighbours::containsKey);
	}

	@Override public Map<Long, Set<String>> getNeighboursOf(final long workplaceId) {
		return HashMap.ofAll(
			EntryStream.of(topologyProvider.neighboursOf(workplaceId)).mapValues(HashSet::ofAll).toMap());
	}

	@Override public void migrate(final Agent agent, final long sourceWorkplace, final long targetWorkplace) {
		requireNonNull(agent);
		checkArgument(sourceWorkplace >= 0);
		checkArgument(targetWorkplace >= 0);

		if (!topologyProvider.areNeighbours(sourceWorkplace, targetWorkplace)) {
			throw new IllegalArgumentException("This workplace is not in neighbourhood"); // XXX: Better exception
		}

		performMigration(agent, targetWorkplace);
	}

	@Override public void migrate(final Agent agent, final long sourceWorkplace, final String neighbourAnnotation) {
		requireNonNull(agent);
		checkArgument(sourceWorkplace >= 0);
		requireNonNull(neighbourAnnotation);

		final Long targetWorkplace = topologyProvider.neighboursOfByAnnotation(sourceWorkplace)
		                                             .get(neighbourAnnotation);

		if (targetWorkplace == null) {
			throw new IllegalArgumentException("Annotation does not point to any workplace"); // XXX: Better exception
		}

		performMigration(agent, targetWorkplace);
	}

	@Override public void migrateUnconditionally(final Agent agent, final long sourceWorkplace,
	                                             final long targetWorkplace) {
		requireNonNull(agent);
		checkArgument(sourceWorkplace >= 0);
		checkArgument(targetWorkplace >= 0);

		performMigration(agent, targetWorkplace);
	}

	@Override public boolean isStopConditionReached() {
		return stopCondition.isReached();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	/**
	 * Performs actual migration without any verification of arguments
	 */
	private void performMigration(final Agent agent, final long targetWorkplace) {
		final WorkerAddress targetAddress = workplacesLocations.get(targetWorkplace);
		logger.debug("Address for Workplace {} is {}", targetWorkplace, targetAddress);
		if (targetAddress == null) {
			throw new IllegalArgumentException("Unknown workplace"); // XXX: Better exception
		}

		logger.debug("Sending {} to {}", agent, targetAddress);
		messenger.send(targetAddress, new MigrationMessage(targetWorkplace, agent));
	}
}
