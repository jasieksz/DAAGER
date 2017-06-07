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

package pl.edu.agh.age.compute.stream.emas;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.AgentsRegistry;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.collection.Seq;

public final class EmasBestAgentsRegistry implements AgentsRegistry<EmasAgent> {

	private static final Logger logger = LoggerFactory.getLogger(EmasBestAgentsRegistry.class);

	/**
	 * This comparator should put the best agent as the last
	 */
	private final Comparator<EmasAgent> agentsComparator;

	@GuardedBy("lock") private final Map<String, Tuple3<Long, Long, Long>> bestSolutionsMap = new HashMap<>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private @Nullable EmasAgent bestAgent = null;

	public EmasBestAgentsRegistry(final Comparator<EmasAgent> agentsComparator) {
		this.agentsComparator = requireNonNull(agentsComparator);
	}

	@Override public void register(final long workplaceId, final long stepNumber, final Seq<EmasAgent> population) {
		checkArgument(stepNumber >= 0);

		lock.writeLock().lock();
		try {
			if (!population.isEmpty()) {
				logger.debug("Registering best agents for workplace {} in step {}", workplaceId, stepNumber);
				registerBestAgents(workplaceId, stepNumber, extractBestAgents(population));
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override public OptionalDouble getBestAgentEvaluation() {
		return (bestAgent != null) ? OptionalDouble.of(bestAgent.solution.fitnessValue()) : OptionalDouble.empty();
	}

	@Override public io.vavr.collection.Map<String, Tuple3<Long, Long, Long>> getBestAgentsStatistics() {
		lock.readLock().lock();
		try {
			return io.vavr.collection.HashMap.ofAll(bestSolutionsMap);
		} finally {
			lock.readLock().unlock();
		}
	}

	private void registerBestAgents(final long workplaceId, final long stepNumber, final Seq<EmasAgent> bestAgents) {
		final EmasAgent randomAgent = bestAgents.get();
		final int comparisonResult = (bestAgent != null) ? agentsComparator.compare(randomAgent, bestAgent) : 1;

		if (comparisonResult > 0) {
			// currently found agents are better than the best known so far
			bestSolutionsMap.clear();
			bestAgent = randomAgent;
			appendBestAgents(workplaceId, stepNumber, bestAgents);
		} else if (comparisonResult == 0) {
			// currently found agents are as good as the best known so far
			appendBestAgents(workplaceId, stepNumber, bestAgents);
		}
	}

	private Seq<EmasAgent> extractBestAgents(final Seq<EmasAgent> population) {
		final Seq<EmasAgent> sortedPopulation = population.sorted(agentsComparator).reverse();
		final EmasAgent bestAgentInPopulation = sortedPopulation.get();
		// At this point, the sortedPopulation sequence has always best agents placed at the sequence beginning
		return sortedPopulation.takeWhile(agent -> agentsComparator.compare(bestAgentInPopulation, agent) == 0);
	}

	private void appendBestAgents(final long workplaceId, final long stepNumber, final Seq<EmasAgent> bestAgents) {
		for (final EmasAgent agent : bestAgents) {
			final String solution = agent.solution.toString();
			Tuple3<Long, Long, Long> solutionStats = bestSolutionsMap.get(solution);
			solutionStats = (solutionStats != null)
			                ? solutionStats.map3(x -> x + 1)
			                : Tuple.of(workplaceId, stepNumber, 1L);
			bestSolutionsMap.put(solution, solutionStats);
		}
	}
}
