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

package pl.edu.agh.age.compute.stream.logging;

import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.compute.stream.Manager;

/**
 * Service responsible for generating log outputs of currently running computations.
 *
 * The log collection is supposed to be run in a separate thread in some specified intervals.
 */
public interface LoggingService {
	/**
	 * Schedules and starts this service in the provided {@link ThreadPool}
	 *
	 * Statistics will be obtained by this service from the passes {@link Manager}.
	 *
	 * @param statisticsManager
	 * 		provider of the statistics
	 * @param threadPool
	 * 		thread pool used to schedule the service
	 */
	void schedule(Manager statisticsManager, ThreadPool threadPool);

	/**
	 * Stops this service
	 *
	 * This operations does not have to be synchronous. Service may be stopped some time in the future after this call.
	 */
	void stop();
}
