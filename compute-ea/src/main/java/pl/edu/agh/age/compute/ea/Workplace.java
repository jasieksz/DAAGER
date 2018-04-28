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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.ea.solution.Solution;

import com.google.common.base.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

import io.vavr.collection.List;
import io.vavr.collection.Map;

public final class Workplace<T extends Solution<?>> implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Workplace.class);

	/**
	 * Must be a non-negative long.
	 */
	private final long id;

	private final List<T> initialPopulation;

	private final AtomicLong step = new AtomicLong(0);

	private final BeforeStepAction<T> beforeStepAction;

	private final Step<T> stepOperations;

	private final AfterStepAction<T, ?> afterStepAction;

	private final Manager manager;

	public Workplace(final long id, final List<T> initialPopulation, final BeforeStepAction<T> beforeStepAction,
	                 final Step<T> stepOperations, final AfterStepAction<T, ?> afterStepAction, final Manager manager) {
		checkArgument(id >= 0, "id must be non-negative");

		this.id = id;
		this.initialPopulation = requireNonNull(initialPopulation);
		this.beforeStepAction = requireNonNull(beforeStepAction);
		this.stepOperations = requireNonNull(stepOperations);
		this.afterStepAction = requireNonNull(afterStepAction);
		this.manager = requireNonNull(manager);
	}

	public final long id() {
		return id;
	}

	@SuppressWarnings("unchecked") @Override public void run() {
		logger.info("[W{}] Workplace {} is starting", id, this);
		logger.debug("[W{}] Initial population: {}", id, initialPopulation);

		List<T> population = initialPopulation;
		while (!Thread.currentThread().isInterrupted() && !manager.isStopConditionReached()) {
			step.incrementAndGet();

			// Before step
			population = beforeStepAction.apply(step.get(), population);

			// Step
			population = stepOperations.stepOn(step.get(), population);
			logger.debug("[W{}] Current population: {}", id, population.length());

			// After step
			final Map<Object, Object> localStats = (Map<Object, Object>)afterStepAction.apply(id, step.get(),
			                                                                                  population);
			manager.postStatistics(id, localStats);
			logger.debug("[W{}] Local stats: {}", id, localStats);
		}

		logger.info("[W{}] Workplace {} finished work in {} steps", id, this, step.get());
		logger.debug("[W{}] Final population: {}", id, population);
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("step", step.get()).toString();
	}
}