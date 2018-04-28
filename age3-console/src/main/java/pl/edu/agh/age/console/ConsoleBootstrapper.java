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

package pl.edu.agh.age.console;

import static com.google.common.base.Preconditions.checkArgument;

import pl.edu.agh.age.client.LifecycleServiceClient;
import pl.edu.agh.age.services.lifecycle.NodeLifecycleService;

import com.google.common.base.Throwables;

import one.util.streamex.StreamEx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

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

	private static final URL[] EMPTY_URLS = {};

	static {
		System.setProperty("logback.statusListenerClass", "ch.qos.logback.core.status.NopStatusListener");
	}

	private static final String LIB_PATH = System.getProperty("age.console.lib.path", "lib/");

	private static final Logger logger = LoggerFactory.getLogger(ConsoleBootstrapper.class);

	private ConsoleBootstrapper() {}

	public static void main(final String... args) {
		System.out.println("Starting AgE console...");
		try {
			if ((args.length > 0) && args[0].equals("standalone")) {
				final String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
				standaloneMain(newArgs);
			} else {
				consoleMain(args);
			}
		} catch (final Exception e) {
			handleException(e);
		}
		logger.info("Exiting");
		System.out.println("Exiting...");
		System.exit(0);
	}

	private static void standaloneMain(final String... args) throws InterruptedException, IOException {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String [] {"spring-standalone.xml"}, false, null)) {
			addJarsFromDir(context, LIB_PATH);
			context.registerShutdownHook();

			final NodeLifecycleService lifecycleService = context.getBean(NodeLifecycleService.class);
			if (lifecycleService == null) {
				logger.error("No node lifecycle service is defined");
				return;
			}

			consoleLoop(context, args);

			logger.info("Destroying cluster");
			context.getBean(LifecycleServiceClient.class).destroyCluster();

			lifecycleService.awaitTermination();
		}
	}

	private static void consoleMain(final String... args) throws IOException {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String [] { "spring-console.xml" }, false, null)) {
			addJarsFromDir(context, LIB_PATH);
			context.registerShutdownHook();
			consoleLoop(context, args);
		}
	}

	private static void handleException(final Exception e) {
		logger.error("Console exception", e);
		final Throwable rootCause = Throwables.getRootCause(e);
		System.out.println();
		System.out.println("[!] I could not initialize the console. The cause was:");
		System.out.println("[!] " + rootCause);
		System.out.println("[!] Have you configured and started the computational cluster?");
		System.out.println();
	}

	private static void consoleLoop(final ConfigurableApplicationContext context, final String[] args) {
		logger.info("Starting console");
		final Console console = context.getBean(Console.class);
		if (console == null) {
			System.out.println("No console is defined. Do you have a correct Spring configuration?");
			logger.error("No console is defined");
			return;
		}
		console.mainLoop(args);
	}


	private static void addJarsFromDir(final ClassPathXmlApplicationContext context, final String libPath)
		throws IOException {
		context.setClassLoader(new URLClassLoader(dirToArrayOfJars(libPath)));
		context.refresh();
	}

	/**
	 * @throws UncheckedIOException
	 * 		in case of any error occurring – cause will contain a detailed error
	 */
	private static URL[] dirToArrayOfJars(final String libPath) throws IOException {
		logger.debug("Adding {} to context", libPath);
		final Path path = Paths.get(libPath);
		final File file = path.toFile();
		if (!file.exists()) {
			return EMPTY_URLS;
		}
		checkArgument(file.isDirectory(), "Passed argument exists but is not a directory");

		try (Stream<Path> list = Files.list(path)) {
			return StreamEx.of(list).filter(p -> p.getFileName().toString().endsWith(".jar")).map(p -> {
				try {
					return p.toUri().toURL();
				} catch (MalformedURLException e) {
					throw new UncheckedIOException(e);
				}
			}).toArray(URL.class);
		}
	}
}
