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

/*
 * Created: 2016-07-11.
 */

package pl.edu.agh.age.compute.stream.configuration;

import static java.util.stream.Collectors.toList;

import pl.edu.agh.age.compute.api.topology.Topology;
import pl.edu.agh.age.compute.stream.AfterStepAction;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.StopCondition;
import pl.edu.agh.age.compute.stream.logging.LoggingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Configuration loader for JavaScript DSL.
 */
public final class NashornLoader {

	private static final Logger logger = LoggerFactory.getLogger(NashornLoader.class);

	private NashornLoader() {}

	public static Configuration load(final String configuration) throws FileNotFoundException {
		logger.info("Loading configuration from {}", configuration);
		return load(new FileInputStream(configuration));
	}

	@SuppressWarnings("unchecked") public static Configuration load(final InputStream configuration) {
		final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			logger.debug("Evaluating base script");
			engine.eval("load('classpath:base.js');");

			logger.debug("Evaluating user script");
			engine.eval(new InputStreamReader(configuration));

			final StopCondition stopCondition = (StopCondition)engine.get("stopCondition");
			logger.debug("Stop condition: {}", stopCondition);

			final LoggingService loggingService = (LoggingService)engine.get("loggingService");
			logger.debug("Logging service: {}", loggingService);

			final Topology<?> topology = (Topology<?>)engine.get("topology");
			logger.debug("Topology: {}", topology);

			final ScriptObjectMirror workplaces = (ScriptObjectMirror)engine.get("workplaces");
			logger.debug("Workplaces array: {}", workplaces);

			if (!workplaces.isArray()) {
				throw new LoadingException("Workplaces in the script are not an array");
			}
			final ScriptObjectMirror[] workplacesArray = workplaces.to(ScriptObjectMirror[].class);

			final List<WorkplaceConfiguration<Agent>> configurations = Arrays.stream(workplacesArray).map(obj -> {
				final Step<Agent> step = ((ScriptObjectMirror)obj.get("step")).to(Step.class);
				final List<Agent> agents = ((javaslang.collection.List<Agent>)obj.get("agents")).toJavaList();
				final AfterStepAction<Agent, Object> after = ((ScriptObjectMirror)obj.get("after")).to(
					AfterStepAction.class);
				// FIXME: Before step?
				return new WorkplaceConfiguration<>(agents, step, after);
			}).collect(toList());

			logger.info("Configuration has been read and built");
			return new Configuration(configurations, stopCondition, loggingService, topology);
		} catch (final ScriptException e) {
			throw new LoadingException("An error happened when parsing the script", e);
		}
	}

	public static class LoadingException extends RuntimeException {

		private static final long serialVersionUID = 6488985976724910073L;

		public LoadingException(final String message) {
			super(message);
		}

		public LoadingException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public LoadingException(final Throwable cause) {
			super(cause);
		}
	}
}
