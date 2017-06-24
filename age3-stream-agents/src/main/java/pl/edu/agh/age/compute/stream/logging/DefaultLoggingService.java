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

package pl.edu.agh.age.compute.stream.logging;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.AgentsRegistry;
import pl.edu.agh.age.compute.stream.Manager;
import pl.edu.agh.age.compute.stream.emas.StatisticsKeys;
import pl.edu.agh.age.compute.stream.problem.EvaluatorCounter;
import pl.edu.agh.age.compute.stream.problem.ProblemDefinition;

import com.google.common.util.concurrent.ListenableScheduledFuture;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.vavr.Tuple3;
import io.vavr.collection.Map;

/**
 * Default implementation of the logging service for stream agents. It logs the data to the SLF4J logger named
 * "stream".
 *
 * Each log file entry consists of an entry type tag and entry values, each separated with a semicolon delimiter
 * (**;**).
 *
 * The supported log entry types are as follows:
 * - `[PD]` - log entry with a one-line problem definition string
 * - `[WH]` - log entry with a one-line definition of properties used in the workplace entries
 * - `[SH]` - log entry with a one-line definition of properties used in the tick summary entries
 * - `[BH]` - log entry with a one-line definition of properties used in best solution entries
 * - `[W]` - log entry for a particular workplace in a tick (each tick consist of *n* such entries, if *n* is the
 * number of all workplaces)
 * - `[S]` - log entry with a tick summary (each tick consists of exactly one entry of this type)
 * - `[B]` - log entry with the best solution (there might be more than one entry of this type in a log, if
 * multiple agents can have the same evaluation)
 *
 * Log entries appear in the log file in the order same as defined above. There is only one log entry for each of types
 * {`[PD]`, `[WH]`, `[SH]`, `[BH]`} and one or more entries for the rest of the types.
 *
 * @see Columns
 * @see Tags
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public final class DefaultLoggingService implements LoggingService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultLoggingService.class);

	private static final Logger stream_logger = LoggerFactory.getLogger("stream");

	public static final String DELIMITER = ";";

	private final LoggingParameters parameters;

	private final EvaluatorCounter evaluatorCounter;

	private final AgentsRegistry<? extends Agent> agentsRegistry;

	private boolean headerLogged = false;

	private long startTime = 0L;

	private @Nullable ListenableScheduledFuture<?> loggerFuture = null;

	@Inject public DefaultLoggingService(final LoggingParameters parameters, final EvaluatorCounter evaluatorCounter,
	                                     final AgentsRegistry<? extends Agent> agentsRegistry) {
		this.parameters = requireNonNull(parameters);
		this.evaluatorCounter = requireNonNull(evaluatorCounter);
		this.agentsRegistry = requireNonNull(agentsRegistry);
	}

	/**
	 * @implNote If the passed `loggingInterval` was zero, then this service will not be scheduled.
	 *
	 * @throws IllegalStateException
	 * 		if this service was already scheduled
	 */
	@Override public void schedule(final Manager statisticsManager, final ThreadPool threadPool) {
		checkState(loggerFuture == null, "Already scheduled");
		final Duration loggingInterval = parameters.loggingInterval();
		if (!loggingInterval.isZero()) {
			logProblemDefinition(parameters.problemDefinition());
			startTime = System.nanoTime();
			loggerFuture = threadPool.scheduleAtFixedRate(() -> {
				logStatistics(statisticsManager.getLocalStatistics(), statisticsManager.getTotalWorkplacesCount());
			}, 0, loggingInterval.toMillis(), TimeUnit.MILLISECONDS);
			loggerFuture.addListener(() -> logBestSolutions(agentsRegistry.getBestAgentsStatistics()),
			                         directExecutor());
		}
	}

	/**
	 * @throws IllegalStateException
	 * 		if this service was not yet scheduled
	 */
	@Override public void stop() {
		checkState(loggerFuture != null, "Not yet scheduled");
		loggerFuture.cancel(true);
	}

	private void logProblemDefinition(final ProblemDefinition problemDefinition) {
		final String problemString = problemDefinition.representation();
		stream_logger.info(String.join(DELIMITER, Tags.PROBLEM_DESCRIPTION_TAG, problemString));
		logger.debug("*** Starting AgE3 platform for solving problem of a following definition: {} ***", problemString);
	}

	private void logStatistics(final Map<Long, Map<Object, Object>> statistics, final int workplacesCount) {
		if (!statistics.isEmpty()) {
			logHeaders(statistics);
			final Long time = System.nanoTime() - startTime;
			for (final Long workplaceId : statistics.keySet()) {
				final Map<Object, Object> stats = statistics.get(workplaceId).get();
				logWorkplaceStatistics(time, workplaceId, workplacesCount, stats);
			}
			logSummaryStatistics(time);
		}
	}

	private void logWorkplaceStatistics(final Long time, final Long workplaceId, final int workplacesCount,
	                                    final Map<Object, Object> workplaceStats) {
		final String[] values = new String[workplaceStats.size() + 3];
		values[0] = Tags.WORKPLACE_ENTRY_TAG;
		values[1] = time.toString();
		values[2] = workplaceId.toString();
		int i = 3;
		for (final Object value : workplaceStats.values()) {
			values[i] = value.toString();
			i++;
		}
		stream_logger.info(String.join(DELIMITER, values));
		logger.info("Workplace {} out of {} processed step number {}", workplaceId, workplacesCount,
		             workplaceStats.get(StatisticsKeys.STEP_NUMBER).getOrElse("<unknown>"));
	}

	private void logSummaryStatistics(final Long time) {
		final String bestEvaluation = Double.toString(agentsRegistry.getBestAgentEvaluation().orElse(Double.NaN));
		final String evaluationCount = Long.toString(evaluatorCounter.get());
		final String[] values = {Tags.TICK_SUMMARY_TAG, time.toString(), bestEvaluation, evaluationCount};
		stream_logger.info(String.join(DELIMITER, values));
	}

	private static void logBestSolutions(final Map<String, Tuple3<Long, Long, Long>> solutionsMap) {
		for (final String solution : solutionsMap.keySet()) {
			final Tuple3<Long, Long, Long> stats = solutionsMap.get(solution).get();
			final String[] values = {Tags.BEST_SOLUTION_TAG, solution, stats._1.toString(), stats._2.toString(),
				stats._3.toString()};
			stream_logger.info(String.join(DELIMITER, values));
		}
	}

	// Header entries

	private void logHeaders(final Map<Long, Map<Object, Object>> statistics) {
		if (!headerLogged) {
			logWorkplaceHeader(statistics.values().iterator().next());
			logSummaryHeader();
			logBestSolutionHeader();
			headerLogged = true;
		}
	}

	private static void logWorkplaceHeader(final Map<Object, Object> workplaceStats) {
		final String[] values = new String[workplaceStats.size() + 3];
		values[0] = Tags.WORKPLACE_ENTRY_HEADER_TAG;
		values[1] = Columns.TIME;
		values[2] = Columns.WORKPLACE_ID;
		int i = 3;
		for (final Object key : workplaceStats.keySet()) {
			values[i] = key.toString();
			i++;
		}
		stream_logger.info(String.join(DELIMITER, values));
	}

	private static void logSummaryHeader() {
		stream_logger.info(String.join(DELIMITER, Columns.SUMMARY_HEADER));
	}

	private static void logBestSolutionHeader() {
		stream_logger.info(String.join(DELIMITER, Columns.BEST_SOLUTION_HEADER));
	}

}
