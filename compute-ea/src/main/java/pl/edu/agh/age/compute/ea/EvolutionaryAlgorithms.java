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

package pl.edu.agh.age.compute.ea;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.api.DistributionUtilities;
import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.compute.api.WorkerAddress;
import pl.edu.agh.age.compute.ea.configuration.Configuration;
import pl.edu.agh.age.compute.ea.configuration.WorkplaceConfiguration;
import pl.edu.agh.age.compute.ea.solution.Solution;

import com.google.common.util.concurrent.ListenableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

/**
 * Main runtime for stream-like EA processing.
 */
public final class EvolutionaryAlgorithms implements Runnable, Manager {

	private static final Logger logger = LoggerFactory.getLogger(EvolutionaryAlgorithms.class);

	private final ThreadPool threadPool;

	private final DistributionUtilities distributionUtilities;

	private final IdGenerator workplaceIdGenerator;

	private final IMap<Long, Map<Object, Object>> statistics;

	private final IMap<Long, WorkerAddress> workplacesLocations;

	private final List<Workplace<Solution<?>>> localWorkplaces;

	private final StopCondition stopCondition;

	@SuppressWarnings("unchecked") @Inject
	public EvolutionaryAlgorithms(final Configuration<Solution<?>> configuration, final ThreadPool threadPool,
	                              final DistributionUtilities distributionUtilities) {
		requireNonNull(configuration);
		this.threadPool = requireNonNull(threadPool);
		this.distributionUtilities = requireNonNull(distributionUtilities);

		workplaceIdGenerator = this.distributionUtilities.getIdGenerator("workplace");
		statistics = this.distributionUtilities.getMap("statistics");
		workplacesLocations = this.distributionUtilities.getMap("workplace-locations");

		// Process configuration
		stopCondition = configuration.stopCondition();
		final List<WorkplaceConfiguration<Solution<?>>> workplaceConfigurations = configuration.workplaces();
		localWorkplaces = workplaceConfigurations.map(c -> c.toWorkplace(workplaceIdGenerator.newId(), this));

		// Update location information
		//final List<Long> localWorkplacesIds = localWorkplaces.map(Workplace::id);

		//localWorkplacesIds.forEach(id -> workplacesLocations.put(id, messenger.address()));
	}

	@Override public void run() {
		logger.info("EA starting");

		// Submit workplaces and wait for finalization
		final java.util.List<ListenableFuture<?>> workplaceFutures = threadPool.submitAll(localWorkplaces.toJavaList());
		try {
			waitForStopCondition();
		} catch (final InterruptedException ignored) {
			logger.info("Processing interrupted");
			Thread.currentThread().interrupt();
		}

		workplaceFutures.forEach(EvolutionaryAlgorithms::cancelWorkplaceFuture);
		logger.info("EA finished");
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

	private void waitForStopCondition() throws InterruptedException {
		while (true) {
			if (stopCondition.isReached(this)) {
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

	@Override public Map<Long, Map<Object, Object>> globalStatistics() {
		return HashMap.ofAll(statistics);
	}

	@Override public <T> Seq<T> statisticsForKey(final Object key, final Class<T> valuesClass) {
		return Stream.ofAll(statistics.values()).flatMap(map -> map.get(key)).map(valuesClass::cast);
	}

	@Override public boolean isStopConditionReached() {
		return stopCondition.isReached(this);
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

}
