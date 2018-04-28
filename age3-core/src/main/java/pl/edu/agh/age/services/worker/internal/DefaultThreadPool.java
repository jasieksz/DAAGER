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

package pl.edu.agh.age.services.worker.internal;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.stream.Collectors.toList;
import static pl.edu.agh.age.util.Runnables.swallowingRunnable;

import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.util.Runnables;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DefaultThreadPool implements ThreadPool {

	private static final Logger logger = LoggerFactory.getLogger(DefaultThreadPool.class);

	private final ListeningScheduledExecutorService service = listeningDecorator(Executors.newScheduledThreadPool(10));

	@Override public List<ListenableFuture<?>> submitAll(final List<? extends Runnable> runnables) {
		return runnables.stream().map(Runnables::swallowingRunnable).map(service::submit).collect(toList());
	}

	@Override
	public ListenableScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay,
	                                                        final long period, final TimeUnit unit) {
		return service.scheduleAtFixedRate(swallowingRunnable(command), initialDelay, period, unit);
	}

	public void shutdownAll() {
		logger.debug("Thread pool shutdown");
		shutdownAndAwaitTermination(service, 5, TimeUnit.SECONDS);
	}
}
