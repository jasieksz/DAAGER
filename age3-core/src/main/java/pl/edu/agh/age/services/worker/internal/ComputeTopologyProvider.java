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

package pl.edu.agh.age.services.worker.internal;

import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.api.TopologyProvider;
import pl.edu.agh.age.compute.api.topology.AnnotatedEdge;
import pl.edu.agh.age.compute.api.topology.FullMeshTopology;
import pl.edu.agh.age.compute.api.topology.Topology;
import pl.edu.agh.age.services.worker.WorkerMessage;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import org.checkerframework.checker.lock.qual.GuardedBy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ThreadSafe
public final class ComputeTopologyProvider implements TopologyProvider<Serializable>, CommunicationFacility {

	private static final Logger logger = LoggerFactory.getLogger(ComputeTopologyProvider.class);

	private final WorkerCommunication workerCommunication;

	private final Map<HazelcastObjectNames.ConfigurationKey, Object> configurationMap;

	/**
	 * Global set of IDs.
	 */
	private final ISet<Serializable> idsSet;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	// Modifying it does not require holding a lock.
	private Topology<Serializable> topology = new FullMeshTopology<>();

	@GuardedBy("lock") private @Nullable Graph<Serializable, AnnotatedEdge> cachedTopology = null;

	@GuardedBy("lock") private boolean needUpgrade = true;

	@Inject
	public ComputeTopologyProvider(final HazelcastInstance hazelcastInstance,
	                               final WorkerCommunication workerCommunication) {
		this.workerCommunication = requireNonNull(workerCommunication);
		// FIXME: Awful, inject shared structures with spring
		configurationMap = hazelcastInstance.getMap(HazelcastObjectNames.CONFIGURATION_MAP_NAME);
		idsSet = hazelcastInstance.getSet(HazelcastObjectNames.COMPUTE_TOPOLOGY_ID_SET);
		idsSet.addItemListener(new IdsListener(), false);
		logger.debug("Compute topology service initialized");
	}

	// TODO
	@Override public <T extends Serializable> boolean onMessage(final WorkerMessage<T> workerMessage) {
		return false;
	}

	// TODO
	@Override public Set<WorkerMessage.Type> subscribedTypes() {
		return ImmutableSet.of();
	}

	// TODO
	@Override public void start() {}

	@Override public void reset() {
		logger.debug("Compute topology reset");
		idsSet.clear();
		topology = new FullMeshTopology<>();
		lock.writeLock().lock();
		try {
			needUpgrade = true;
			cachedTopology = null;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override public void setTopology(final Topology<Serializable> topology) {
		this.topology = requireNonNull(topology);
		markForUpgrade();
		logger.debug("Compute topology set to {}", topology);
	}

	@Override public void addNodes(final Set<Serializable> ids) {
		requireNonNull(ids);

		logger.debug("Adding new nodes to topology - {}", ids);
		// 1. Notify all
		// 2. Update graph
		// 3. Wait for finished update (TODO)
		idsSet.addAll(ids);

		markForUpgrade();
	}

	@SuppressWarnings("LockAcquiredButNotSafelyReleased") @Override
	public Map<Serializable, Set<String>> neighboursOf(final Serializable id) {
		requireNonNull(id);

		lock.readLock().lock();
		if (needUpgrade) {
			lock.readLock().unlock();
			lock.writeLock().lock();
			try {
				if (needUpgrade) {
					logger.debug("Upgrading topology");
					cachedTopology = topology.apply(idsSet);
					needUpgrade = false;
				}
				lock.readLock().lock();
			} finally {
				lock.writeLock().unlock();
			}
		}

		try {
			return StreamEx.of(cachedTopology.outgoingEdgesOf(id))
			               .toMap(cachedTopology::getEdgeTarget, AnnotatedEdge::annotations);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override public Map<String, Serializable> neighboursOfByAnnotation(final Serializable id) {
		final Map<Serializable, Set<String>> objectSetMap = neighboursOf(id);
		return EntryStream.of(objectSetMap).invert().flatMapKeys(StreamEx::of).toMap();
	}

	@Override public boolean areNeighbours(final Serializable first, final Serializable second) {
		return neighboursOf(first).containsKey(second);
	}

	private void markForUpgrade() {
		logger.debug("Marking topology for upgrade");
		lock.writeLock().lock();
		try {
			needUpgrade = true;
		} finally {
			lock.writeLock().unlock();
		}
	}

	private final class IdsListener implements ItemListener<Serializable> {
		@Override public void itemAdded(final ItemEvent<Serializable> item) {
			markForUpgrade();
		}

		@Override public void itemRemoved(final ItemEvent<Serializable> item) {
			markForUpgrade();
		}
	}
}

