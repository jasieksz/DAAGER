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
package pl.edu.agh.age.node;

import static java.util.Objects.isNull;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.client.WorkerServiceClient;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;
import pl.edu.agh.age.services.worker.internal.SpringConfiguration;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Bootstrapper for the node.
 *
 * When run without arguments, the node will be waiting for commands from the console or from other nodes.
 *
 * When arguments are provided, each of them is treated as a path to the spring configuration for the computation.
 * They are run one after another within a cluster and the cluster is destroyed after the last file.
 *
 * A node existence is bound to the **NodeLifecycleService** and the node is destroyed when this service is stopped.
 *
 * @see NodeLifecycleService
 */
@SuppressWarnings("CallToSystemExit")
public final class NodeBootstrapper {

	private static final Logger logger = LoggerFactory.getLogger(NodeBootstrapper.class);

	private NodeBootstrapper() {}

	public static void main(final String... args) throws InterruptedException, IOException {
		final @Nullable String property = System.getProperty("age.node.config");
		final String configName = (property != null) ? property : "spring-node.xml";
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(configName)) {
			context.registerShutdownHook();
			final NodeLifecycleService lifecycleService = context.getBean(NodeLifecycleService.class);
			if (isNull(lifecycleService)) {
				logger.error("No node lifecycle service is defined");
				return;
			}

			if (args.length > 0) {
				logger.info("Running batch configuration");
				final WorkerServiceClient workerServiceClient = context.getBean(WorkerServiceClient.class);

				for (final String arg : args) {
					logger.info("Loading: {}", arg);
					workerServiceClient.prepareConfiguration(SpringConfiguration.fromFilesystem(arg));
					TimeUnit.SECONDS.sleep(1);
					workerServiceClient.startComputation();
					TimeUnit.SECONDS.sleep(1);
					while (workerServiceClient.isComputationRunning()) {
						TimeUnit.SECONDS.sleep(1);
					}
					workerServiceClient.cleanConfiguration();
				}

				logger.info("Destroying cluster");
				context.getBean(LifecycleServiceClient.class).destroyCluster();
			}
			lifecycleService.awaitTermination();
		}
		logger.info("Exiting");
		System.exit(0);
	}
}
