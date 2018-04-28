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

import static com.google.common.base.MoreObjects.toStringHelper;

import pl.edu.agh.age.compute.api.QueryProcessor;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.worker.WorkerMessage;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class DefaultQueryProcessor<T extends Serializable> implements QueryProcessor<T>, CommunicationFacility {

	private static final Logger log = LoggerFactory.getLogger(DefaultQueryProcessor.class);

	private final WorkerCommunication workerCommunication;

	private final NodeIdentityService identityService;

	private final ReplicatedMap<String, T> replicatedMap;

	@Inject
	public DefaultQueryProcessor(final NodeIdentityService identityService, final HazelcastInstance hazelcastInstance,
	                             final WorkerCommunication workerCommunication) {
		this.identityService = identityService;
		this.workerCommunication = workerCommunication;
		replicatedMap = hazelcastInstance.getReplicatedMap("query-cache");
	}

	@PreDestroy private void destroy() throws InterruptedException {
		log.debug("Destroying Query Processor");
		// XXX: Replicated map does not support eviction yet
		replicatedMap.remove(identityService.nodeId());
		TimeUnit.SECONDS.sleep(1L); // Give it a chance to propagate
	}

	@Override public Stream<T> query() {
		return replicatedMap.values().stream();
	}

	@SuppressWarnings("FutureReturnValueIgnored") @Override public void schedule(final Callable<T> callable) {
		workerCommunication.scheduleAtFixedRate(() -> {
			try {
				final T call = callable.call();
				replicatedMap.put(identityService.nodeId(), call, 10L, TimeUnit.SECONDS);
			} catch (final RuntimeException | Error e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}, 0L, 1L, TimeUnit.SECONDS);
	}

	@Override public <V extends Serializable> boolean onMessage(final WorkerMessage<V> workerMessage) {
		assert false : "onMessage should not be called";
		throw new UnsupportedOperationException("onMessage is not supported for DefaultQueryProcessor");
	}

	@Override public Set<WorkerMessage.Type> subscribedTypes() {
		return ImmutableSet.of();
	}

	@Override public void start() {
		log.debug("Starting Query Processor");
	}

	@Override public void reset() {
		log.debug("Query processor reset");
		replicatedMap.clear();
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}

