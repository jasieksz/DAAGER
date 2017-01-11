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
import pl.edu.agh.age.services.worker.internal.configuration.SpringConfiguration;
import pl.edu.agh.age.util.NodeSystemProperties;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;

import one.util.streamex.StreamEx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
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

	static {
		System.setProperty("logback.statusListenerClass", "ch.qos.logback.core.status.NopStatusListener");
	}

	private static final Logger logger = LoggerFactory.getLogger(NodeBootstrapper.class);

	private NodeBootstrapper() {}

	public static void main(final String... args) throws InterruptedException, IOException {
		final String configName = NodeSystemProperties.CONFIG.get();
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
					final List<String> files = Splitter.on(',').omitEmptyStrings().splitToList(arg);
					final List<Resource> propertiesFiles = StreamEx.of(files).map(context::getResource).toList();
					final Resource configuration = propertiesFiles.remove(0);
					final Properties properties = new Properties();
					for (final Resource propertiesFile : propertiesFiles) {
						logger.debug("Loading passedProperties from {}", propertiesFile);
						properties.load(new InputStreamReader(propertiesFile.getInputStream(), Charsets.UTF_8));
					}

					workerServiceClient.prepareConfiguration(new SpringConfiguration(configuration, properties));
					TimeUnit.SECONDS.sleep(1);
					workerServiceClient.startComputation();

					workerServiceClient.waitForComputationEnd();
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
