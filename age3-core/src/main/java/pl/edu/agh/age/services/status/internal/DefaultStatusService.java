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

package pl.edu.agh.age.services.status.internal;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import pl.edu.agh.age.services.ServiceFailureEvent;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;
import pl.edu.agh.age.services.status.Status;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.s;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class DefaultStatusService implements SmartLifecycle {

	private static final @s long UPDATE_PERIOD_IN_S = 1L;

	private static final Logger logger = LoggerFactory.getLogger(DefaultStatusService.class);

	private final ListeningScheduledExecutorService executorService = listeningDecorator(
		newSingleThreadScheduledExecutor());

	private final AtomicBoolean running = new AtomicBoolean(false);

	private final List<Throwable> collectedErrors = newArrayListWithCapacity(10);

	private final NodeIdentityService identityService;

	private final NodeLifecycleService lifecycleService;

	private final EventBus eventBus;

	private final String nodeId;

	private final IMap<String, Status> statusMap;

	@Inject
	public DefaultStatusService(final NodeIdentityService identityService, final NodeLifecycleService lifecycleService,
	                            final HazelcastInstance hazelcastInstance, final EventBus eventBus) {
		this.identityService = identityService;
		this.lifecycleService = lifecycleService;
		this.eventBus = eventBus;

		nodeId = identityService.nodeId();
		statusMap = hazelcastInstance.getMap(HazelcastObjectNames.MAP_NAME);
		eventBus.register(this);
	}

	@Override public boolean isAutoStartup() {
		return true;
	}

	@Override public void stop(final Runnable callback) {
		stop();
		callback.run();
	}

	@Override public void start() {
		logger.debug("Status service starting");

		running.set(true);
		final ListenableScheduledFuture<?> mapUpdateTask = executorService.scheduleAtFixedRate(this::updateMap,
		                                                                                       UPDATE_PERIOD_IN_S,
		                                                                                       UPDATE_PERIOD_IN_S,
		                                                                                       TimeUnit.SECONDS);
		Futures.addCallback(mapUpdateTask, new MapUpdateCallback(), directExecutor());

		logger.info("Status service started");
	}

	@Override public void stop() {
		logger.debug("Status service stopping");

		running.set(false);
		shutdownAndAwaitTermination(executorService, 10L, TimeUnit.SECONDS);

		logger.info("Status service stopped");
	}

	@Override public boolean isRunning() {
		return running.get();
	}

	@Override public int getPhase() {
		return 0;
	}

	private void updateMap() {
		final DefaultStatus.Builder statusBuilder = DefaultStatus.Builder.create();
		statusBuilder.addErrors(collectedErrors);
		statusMap.put(nodeId, statusBuilder.buildStatus());
	}

	// Event handlers

	@Subscribe public void handleServiceFailureEvent(final ServiceFailureEvent event) {
		logger.debug("Service failure event: {}", event);
		collectedErrors.add(event.cause());
	}

	private static final class MapUpdateCallback implements FutureCallback<Object> {
		@Override public void onSuccess(final @Nullable Object result) {
			// Empty
		}

		@Override public void onFailure(final Throwable t) {
			logger.error("Map update failed", t);
		}
	}
}
