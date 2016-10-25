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

package pl.edu.agh.age.services.topology.internal;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.shutdownAndAwaitTermination;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static pl.edu.agh.age.services.topology.internal.TopologyMessage.Type.MASTER_ELECTED;
import static pl.edu.agh.age.services.topology.internal.TopologyMessage.Type.TOPOLOGY_SELECTED;

import pl.edu.agh.age.services.discovery.DiscoveryEvent;
import pl.edu.agh.age.services.discovery.DiscoveryService;
import pl.edu.agh.age.services.identity.NodeDescriptor;
import pl.edu.agh.age.services.identity.NodeIdentityService;
import pl.edu.agh.age.services.lifecycle.NodeDestroyedEvent;
import pl.edu.agh.age.services.topology.TopologyService;
import pl.edu.agh.age.services.topology.processors.TopologyProcessor;
import pl.edu.agh.age.util.fsm.FSM;
import pl.edu.agh.age.util.fsm.StateMachineService;
import pl.edu.agh.age.util.fsm.StateMachineServiceBuilder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public final class DefaultTopologyService implements SmartLifecycle, TopologyService {

	public static final String CONFIG_MAP_NAME = "topology/config";

	public static final String CHANNEL_NAME = "topology/channel";

	/**
	 * States of the topology service.
	 */
	private enum State {
		OFFLINE,
		STARTING,
		MASTER_ELECTED_MASTER,
		MASTER_ELECTED_SLAVE,
		// Only in this state we can communicate
		WITH_TOPOLOGY,
		FAILED,
		TERMINATED
	}

	/**
	 * Events that can occur in the service.
	 */
	private enum Event {
		START,
		STARTED,
		MEMBERSHIP_CHANGED,
		TOPOLOGY_TYPE_CHANGED,
		TOPOLOGY_CONFIGURED,
		ERROR,
		STOP
	}

	private static final Logger logger = LoggerFactory.getLogger(DefaultTopologyService.class);

	private final ListeningScheduledExecutorService executorService = listeningDecorator(
		newSingleThreadScheduledExecutor());

	private final DiscoveryService discoveryService;

	private final NodeIdentityService identityService;

	private final EventBus eventBus;

	private final List<TopologyProcessor> topologyProcessors;

	private TopologyProcessor currentTopologyProcessor;

	private final IMap<String, Object> runtimeConfig;

	private final ITopic<TopologyMessage> topic;

	private ICountDownLatch latch;

	private final StateMachineService<State, Event> service;

	private boolean master;

	private @Nullable String listenerKey;

	@Nullable private DirectedGraph<String, DefaultEdge> cachedTopology;

	@Inject
	public DefaultTopologyService(final DiscoveryService discoveryService, final NodeIdentityService identityService,
	                              final EventBus eventBus, final List<TopologyProcessor> topologyProcessors,
	                              final HazelcastInstance hazelcastInstance) {
		this.discoveryService = discoveryService;
		this.identityService = identityService;
		this.eventBus = eventBus;
		this.topologyProcessors = topologyProcessors;
		// Obtain dependencies
		runtimeConfig = hazelcastInstance.getMap(CONFIG_MAP_NAME);
		topic = hazelcastInstance.getTopic(CHANNEL_NAME);

		logger.debug("Constructing DefaultTopologyService");
		//@formatter:off
		service = StateMachineServiceBuilder
			.withStatesAndEvents(State.class, Event.class)
			.withName("topology")
			.startWith(State.OFFLINE)
			.terminateIn(State.TERMINATED, State.FAILED)

			.in(State.OFFLINE)
				.on(Event.START).execute(this::internalStart).goTo(State.STARTING)
				.on(Event.MEMBERSHIP_CHANGED).goTo(State.OFFLINE) // Ignore event
				.commit()

			.in(State.STARTING)
				.on(Event.STARTED).execute(this::electMaster).goTo(State.MASTER_ELECTED_MASTER, State.MASTER_ELECTED_SLAVE)
				.on(Event.MEMBERSHIP_CHANGED).goTo(State.STARTING)
				.commit()

			.in(State.MASTER_ELECTED_MASTER)
				.on(Event.MEMBERSHIP_CHANGED).execute(this::electMaster).goTo(State.MASTER_ELECTED_MASTER, State.MASTER_ELECTED_SLAVE)
				.on(Event.TOPOLOGY_TYPE_CHANGED).execute(this::topologyChanged).goTo(State.WITH_TOPOLOGY)
				.commit()

			.in(State.MASTER_ELECTED_SLAVE)
				.on(Event.MEMBERSHIP_CHANGED).execute(this::electMaster).goTo(State.MASTER_ELECTED_MASTER, State.MASTER_ELECTED_SLAVE)
				.on(Event.TOPOLOGY_TYPE_CHANGED).goTo(State.MASTER_ELECTED_SLAVE)
				.on(Event.TOPOLOGY_CONFIGURED).execute(this::topologyConfigured).goTo(State.WITH_TOPOLOGY)
				.commit()

			.in(State.WITH_TOPOLOGY)
				.on(Event.MEMBERSHIP_CHANGED).execute(this::electMaster).goTo(State.MASTER_ELECTED_MASTER, State.MASTER_ELECTED_SLAVE)
				.on(Event.TOPOLOGY_TYPE_CHANGED).execute(this::topologyChanged).goTo(State.WITH_TOPOLOGY)
				.on(Event.TOPOLOGY_CONFIGURED).execute(this::topologyConfigured).goTo(State.WITH_TOPOLOGY)
				.commit()

			.inAnyState()
				.on(Event.STOP).execute(this::internalStop).goTo(State.TERMINATED)
				.on(Event.ERROR).execute(this::handleError).goTo(State.FAILED)
				.commit()

			.ifFailed()
				.fireAndCall(Event.ERROR, new ExceptionHandler())

			.withEventBus(eventBus)
			.build();
		//@formatter:on
	}

	@Override public boolean isAutoStartup() {
		return true;
	}

	@Override public void stop(final Runnable callback) {
		stop();
		callback.run();
	}

	@Override public void start() {
		service.fire(Event.START);
	}

	@Override public void stop() {
		service.fire(Event.STOP);
	}

	@Override public boolean isRunning() {
		return service.isRunning();
	}

	@Override public int getPhase() {
		return 0;
	}

	private void internalStart(final FSM<State, Event> fsm) {
		logger.debug("Topology service starting.");
		logger.debug("Known topologies: {}.", topologyProcessors);
		assert !topologyProcessors.isEmpty() : "No topology processors.";
		assert identityService.isCompute() : "This implementation is only for compute nodes.";

		topic.addMessageListener(new DistributedMessageListener());
		eventBus.register(this);

		logger.info("Topology service started.");
		service.fire(Event.STARTED);
	}

	private void internalStop(final FSM<State, Event> fsm) {
		logger.debug("Topology service stopping.");
		shutdownAndAwaitTermination(executorService, 10L, TimeUnit.SECONDS);
		logger.info("Topology service stopped.");
	}

	private void handleError(final FSM<State, Event> fsm) {

	}

	// Topology methods

	/**
	 * Simple master selection. We rely on Hazelcast, so we just need to perform local, deterministic selection.
	 *
	 * In this case we selects the node with the largest nodeId.
	 */
	private void electMaster(final FSM<State, Event> fsm) {
		logger.debug("Locally selecting master.");

		final Set<NodeDescriptor> computeNodes = computeNodes();
		final Optional<NodeDescriptor> maxIdentity = computeNodes.parallelStream()
		                                                         .max(Comparator.comparing(NodeDescriptor::id));
		logger.debug("Max identity is {}.", maxIdentity);

		assert maxIdentity.isPresent();

		if (identityService.nodeId().equals(maxIdentity.get().id())) {
			logger.debug("I am master.");
			master = true;
			runtimeConfig.put(ConfigKeys.MASTER, identityService.nodeId());

			// Select initial topology type if this is the first election
			if (!runtimeConfig.containsKey(ConfigKeys.TOPOLOGY_TYPE)) {
				logger.debug("Seems to be the first election. Selecting topology.");
				final Optional<TopologyProcessor> selectedProcessor = topologyProcessors.parallelStream()
				                                                                        .max(Comparator.comparing(
					                                                                        TopologyProcessor::priority));
				assert selectedProcessor.isPresent();

				currentTopologyProcessor = selectedProcessor.get();
				logger.debug("Selected initial topology: {}.", currentTopologyProcessor);
				runtimeConfig.put(ConfigKeys.TOPOLOGY_TYPE, currentTopologyProcessor.name());
			}
			listenerKey = runtimeConfig.addEntryListener(new TopologyTypeChangeListener(), ConfigKeys.TOPOLOGY_TYPE,
			                                             true);

			service.fire(Event.TOPOLOGY_TYPE_CHANGED);
			fsm.goTo(State.MASTER_ELECTED_MASTER);
			topic.publish(TopologyMessage.createWithoutPayload(MASTER_ELECTED));

		} else {
			logger.debug("I am slave.");
			master = false;
			if (listenerKey != null) {
				runtimeConfig.removeEntryListener(listenerKey);
			}

			fsm.goTo(State.MASTER_ELECTED_SLAVE);
		}
	}

	/**
	 * Called when topology has changed:
	 * <ul>
	 * <li>new member</li>
	 * <li>member removal</li>
	 * <li>type changed</li>
	 * </ul>
	 *
	 * Executed only on master.
	 */
	private void topologyChanged(final FSM<State, Event> stateEventFSM) {
		assert master;
		logger.debug("Topology initialization.");

		final String processorName = topologyType().get();
		logger.debug("Processor name: {}.", processorName);

		final Optional<TopologyProcessor> topologyProcessor = getTopologyProcessorWithName(processorName);
		assert topologyProcessor.isPresent();
		currentTopologyProcessor = topologyProcessor.get();

		final Set<NodeDescriptor> computeNodes = computeNodes();
		cachedTopology = currentTopologyProcessor.createGraphFrom(computeNodes);
		logger.debug("Topology: {}.", cachedTopology);
		runtimeConfig.put(ConfigKeys.TOPOLOGY_GRAPH, cachedTopology);
		topic.publish(TopologyMessage.createWithoutPayload(TOPOLOGY_SELECTED));
	}

	/**
	 * Called on all nodes when the topology has been configured by master.
	 */
	private void topologyConfigured(final FSM<State, Event> stateEventFSM) {
		assert !master || (currentTopologyProcessor != null) : "Current topology processor null for master";
		assert runtimeConfig.get(ConfigKeys.TOPOLOGY_GRAPH) != null : "No topology graph in config";

		logger.debug("Topology has been configured. Caching the graph.");
		cachedTopology = getCurrentTopologyGraph();
	}

	private Optional<TopologyProcessor> getTopologyProcessorWithName(final String processorName) {
		assert processorName != null;
		return topologyProcessors.parallelStream()
		                         .filter(processor -> processor.name().equals(processorName))
		                         .findFirst();
	}

	 private @Nullable DirectedGraph<String, DefaultEdge> getCurrentTopologyGraph() {
		return (DirectedGraph<String, DefaultEdge>)runtimeConfig.get(ConfigKeys.TOPOLOGY_GRAPH);
	}

	@Override public Optional<String> masterId() {
		return Optional.ofNullable((String)runtimeConfig.get(ConfigKeys.MASTER));
	}

	@Override public boolean isLocalNodeMaster() {
		final Optional<String> masterId = masterId();
		return masterId.isPresent() && masterId.get().equals(identityService.nodeId());
	}

	@Override public boolean hasTopology() {
		return service.isInState(State.WITH_TOPOLOGY);
	}

	@Override public Optional<DirectedGraph<String, DefaultEdge>> topologyGraph() {
		return Optional.ofNullable(cachedTopology);
	}

	@Override public Optional<String> topologyType() {
		return Optional.ofNullable((String)runtimeConfig.get(ConfigKeys.TOPOLOGY_TYPE));
	}

	@Override public Set<String> neighbours() {
		if (!hasTopology()) {
			throw new IllegalStateException("Topology not ready.");
		}

		final DirectedGraph<String, DefaultEdge> graph = getCurrentTopologyGraph();
		final Set<DefaultEdge> outEdges = graph.outgoingEdgesOf(identityService.nodeId());
		return outEdges.stream().map(graph::getEdgeTarget).collect(Collectors.toSet());
	}

	@Subscribe public void membershipChange(final DiscoveryEvent event) {
		logger.debug("Membership change: {}.", event);
		service.fire(Event.MEMBERSHIP_CHANGED);
	}

	protected Set<NodeDescriptor> computeNodes() {
		return discoveryService.membersMatching("type = 'compute'");
	}

	private static class ConfigKeys {
		public static final String MASTER = "master";

		public static final String TOPOLOGY_GRAPH = "topologyGraph";

		public static final String TOPOLOGY_TYPE = "topologyType";
	}

	@Subscribe public void handleNodeDestroyedEvent(final NodeDestroyedEvent event) {
		logger.debug("Got event: {}.", event);
		service.fire(Event.STOP);
	}

	@Override public String toString() {
		return toStringHelper(this).add("state", service.currentState())
		                           .add("topology-type", topologyType())
		                           .add("graph", topologyGraph())
		                           .toString();
	}

	private class TopologyTypeChangeListener extends EntryAdapter<String, Object> {
		@Override public void entryUpdated(final EntryEvent<String, Object> event) {
			logger.info("Topology type updated: {}.", event);
			service.fire(Event.TOPOLOGY_TYPE_CHANGED);
		}
	}

	private class DistributedMessageListener implements MessageListener<TopologyMessage> {
		@Override public void onMessage(final Message<TopologyMessage> message) {
			logger.debug("Distributed event: {}", message);
			final TopologyMessage topologyMessage = message.getMessageObject();
			switch (topologyMessage.getType()) {
				case TOPOLOGY_SELECTED:
					service.fire(Event.TOPOLOGY_CONFIGURED);
					break;
			}
		}
	}

	private class ExceptionHandler implements Consumer<Throwable> {

		@Override public void accept(final Throwable throwable) {
			logger.error("Exception", throwable);
		}
	}
}
