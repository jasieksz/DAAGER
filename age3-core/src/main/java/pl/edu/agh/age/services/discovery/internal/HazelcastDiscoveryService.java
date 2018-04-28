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

package pl.edu.agh.age.services.discovery.internal;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import pl.edu.agh.age.services.discovery.DiscoveryService;
import pl.edu.agh.age.services.discovery.DiscoveryServiceStoppingEvent;
import pl.edu.agh.age.services.discovery.MemberAddedEvent;
import pl.edu.agh.age.services.discovery.MemberRemovedEvent;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.util.Runnables;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.query.SqlPredicate;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.s;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class HazelcastDiscoveryService implements SmartLifecycle, DiscoveryService {

	private static final @s long UPDATE_PERIOD_IN_S = 10L;

	private static final Logger logger = LoggerFactory.getLogger(HazelcastDiscoveryService.class);

	private final ListeningScheduledExecutorService executorService = listeningDecorator(
		newSingleThreadScheduledExecutor());

	private final AtomicBoolean running = new AtomicBoolean(false);

	private final HazelcastInstance hazelcastInstance;

	private final NodeIdentityService identityService;

	private final EventBus eventBus;

	private final IMap<String, NodeDescriptor> members;

	private final String nodeId;

	private final String entryListenerId;

	@Inject
	public HazelcastDiscoveryService(final HazelcastInstance hazelcastInstance, final EventBus eventBus,
	                                 final NodeIdentityService identityService) {
		this.hazelcastInstance = hazelcastInstance;
		this.eventBus = eventBus;
		this.identityService = identityService;
		nodeId = identityService.nodeId();
		members = hazelcastInstance.getMap(HazelcastObjectNames.MEMBERS_MAP);
		entryListenerId = members.addEntryListener(new NeighbourMapListener(), true);
	}

	@Override public boolean isAutoStartup() {
		return true;
	}

	@Override public void stop(final Runnable callback) {
		stop();
		callback.run();
	}

	@Override public void start() {
		logger.debug("Discovery service starting");
		logger.debug("Hazelcast instance: {}", hazelcastInstance);
		running.set(true);
		hazelcastInstance.getLifecycleService().addLifecycleListener(this::onHazelcastStateChange);
		logger.debug("Waiting for initialization to complete");
		updateMap();
		final ListenableScheduledFuture<?> mapUpdateTask = executorService.scheduleAtFixedRate(
			Runnables.withThreadName("discovery-map-update", this::updateMap), UPDATE_PERIOD_IN_S, UPDATE_PERIOD_IN_S,
			TimeUnit.SECONDS);
		Futures.addCallback(mapUpdateTask, new MapUpdateCallback(), directExecutor());
		logger.info("Discovery service started");
	}

	@SuppressWarnings("ResultOfMethodCallIgnored") @Override public void stop() {
		logger.debug("Discovery service stopping");
		cleanUp();
		shutdownAndAwaitTermination(executorService, UPDATE_PERIOD_IN_S, TimeUnit.SECONDS);
		running.set(false);
		logger.info("Discovery service stopped");
	}

	@Override public boolean isRunning() {
		return running.get();
	}

	@Override public int getPhase() {
		return Integer.MIN_VALUE + 1;
	}

	// Interface methods

	@Override public Set<NodeDescriptor> membersMatching(final String criteria) {
		return ImmutableSet.copyOf(members.values(new SqlPredicate(requireNonNull(criteria))));
	}

	@Override public Set<NodeDescriptor> allMembers() {
		return ImmutableSet.copyOf(members.values());
	}

	@Override public Optional<NodeDescriptor> memberWithId(final String id) {
		final @Nullable NodeDescriptor descriptor = members.get(requireNonNull(id));
		if (isNull(descriptor)) {
			logger.debug("No such member: {}", id);
		}
		return Optional.ofNullable(descriptor);
	}

	// Actions

	private void updateMap() {
		logger.debug("Updating my info in the members map: {}", nodeId);
		members.set(nodeId, identityService.descriptor());
		logger.debug("Finished update");
	}

	private void cleanUp() {
		members.removeEntryListener(entryListenerId);
		logger.debug("Deleting myself from the members map");
		members.delete(nodeId);
	}

	// Listeners

	// Wait for "shutting down" event to clean up
	private void onHazelcastStateChange(final LifecycleEvent event) {
		assert nonNull(event);

		logger.debug("Hazelcast lifecycle event: {}", event);
		if (event.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
			eventBus.post(new DiscoveryServiceStoppingEvent());
			cleanUp();
		}
	}

	private static final class MapUpdateCallback implements FutureCallback<Object> {
		@Override public void onSuccess(final Object result) {
			// Empty
		}

		@Override public void onFailure(final Throwable t) {
			logger.error("Map update failed", t);
		}
	}

	private final class NeighbourMapListener
		implements EntryAddedListener<String, NodeDescriptor>, EntryRemovedListener<String, NodeDescriptor> {

		@Override public void entryAdded(final EntryEvent<String, NodeDescriptor> event) {
			logger.debug("NeighbourMapListener add event: {}", event);
			eventBus.post(new MemberAddedEvent(event.getKey(), event.getValue().type()));
		}

		@Override public void entryRemoved(final EntryEvent<String, NodeDescriptor> event) {
			logger.debug("NeighbourMapListener remove event: {}", event);
			eventBus.post(new MemberRemovedEvent(event.getKey()));
		}
	}
}
