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
 * No-op implementation of the logging service used when no logging is necessary
 */
public final class NoOpLoggingService implements LoggingService {

	@Override public void schedule(final Manager statisticsManager, final ThreadPool threadPool) {}

	@Override public void stop() {}
}
