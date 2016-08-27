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

package pl.edu.agh.age.console;

import static java.util.Objects.isNull;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;

import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstrapper for the console.
 *
 * It provides two modes:
 *
 * * a shell only, when the console attaches to the running cluster as a non-participating client,
 * * a standalone node, when the shell is run alongside a single node.
 *
 * The shell-only mode is started by default. For a standalone node mode run the bootstrapper with the first argument
 * set to `standalone`.
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToSystemExit", "ThrowableResultOfMethodCallIgnored"})
public final class ConsoleBootstrapper {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleBootstrapper.class);

	private ConsoleBootstrapper() {}

	public static void main(final String... args) throws InterruptedException {
		System.out.println("Starting AgE console...");
		try {
			if ((args.length == 1) && args[0].equals("standalone")) {
				standaloneMain();
			} else {
				consoleMain();
			}
		} catch (final BeanCreationException e) {
			handleException(e);
		}
		logger.info("Exiting");
		System.out.println("Exiting...");
		System.exit(0);
	}

	private static void standaloneMain() throws InterruptedException {
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("spring-standalone.xml")) {
			context.registerShutdownHook();

			final NodeLifecycleService lifecycleService = context.getBean(NodeLifecycleService.class);
			if (isNull(lifecycleService)) {
				logger.error("No node lifecycle service is defined");
				return;
			}

			consoleLoop(context);

			logger.info("Destroying cluster");
			context.getBean(LifecycleServiceClient.class).destroyCluster();

			lifecycleService.awaitTermination();
		}
	}

	private static void consoleMain() {
		try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("spring-console.xml")) {
			context.registerShutdownHook();
			consoleLoop(context);
		}
	}

	private static void handleException(final Exception e) {
		logger.error("Console exception", e);
		final Throwable rootCause = Throwables.getRootCause(e);
		System.out.println("I could not initialize the console. The cause was:");
		System.out.println(rootCause.getLocalizedMessage());
		System.out.println("Have you configured and started the computational cluster?");
	}

	private static void consoleLoop(final ConfigurableApplicationContext context) {
		logger.info("Starting console");
		final Console console = context.getBean(Console.class);
		if (isNull(console)) {
			System.out.println("No console is defined. Do you have a correct spring configuration?");
			logger.error("No console is defined.");
			return;
		}
		console.mainLoop();
	}
}
