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

package pl.edu.agh.age.services.worker.internal.task;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static pl.edu.agh.age.util.Runnables.withThreadName;

import pl.edu.agh.age.compute.api.Pauseable;
import pl.edu.agh.age.services.worker.FailedComputationSetupException;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ByteArrayResource;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Builds a single compute task.
 *
 * It is responsible for data consistency of the task.
 */
@ThreadSafe
public final class TaskBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TaskBuilder.class);

	private final AbstractApplicationContext springContext;

	private boolean configured = false;

	private TaskBuilder(final AbstractApplicationContext springContext) {
		assert springContext != null;
		this.springContext = springContext;
	}

	public static TaskBuilder fromClass(final String className) throws FailedComputationSetupException {
		requireNonNull(className);

		try {
			logger.debug("Setting up task from class {}", className);
			final AnnotationConfigApplicationContext taskContext = new AnnotationConfigApplicationContext();

			// Configure task
			final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(className);
			taskContext.registerBeanDefinition("runnable", builder.getBeanDefinition());
			logger.debug("Task setup finished");

			return new TaskBuilder(taskContext);
		} catch (final BeanCreationException e) {
			logger.error("Cannot create the task from class", e);
			throw new FailedComputationSetupException("Cannot create the task from class", e);
		}
	}

	public static TaskBuilder fromString(final String configuration, final Map<String, Object> properties)
		throws FailedComputationSetupException {
		requireNonNull(configuration);
		requireNonNull(properties);

		try {
			logger.debug("Setting up task from config {}", configuration.substring(0, 50));
			logger.debug("Properties are: {}", properties);
			final GenericXmlApplicationContext taskContext = new GenericXmlApplicationContext();

			final MutablePropertySources propertySources = new MutablePropertySources();
			propertySources.addFirst(new MapPropertySource("local", properties));
			final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setPropertySources(propertySources);
			taskContext.addBeanFactoryPostProcessor(configurer);
			taskContext.load(new ByteArrayResource(configuration.getBytes()));
			logger.debug("Task setup finished");

			return new TaskBuilder(taskContext);
		} catch (final BeanCreationException e) {
			logger.error("Cannot create the task from file", e);
			throw new FailedComputationSetupException("Cannot create the task from file", e);
		}
	}

	public boolean isConfigured() {
		return configured;
	}

	public AbstractApplicationContext springContext() {
		return springContext;
	}

	public void registerSingleton(final Object bean) {
		assert bean != null;
		checkState(!configured, "Task is already configured.");

		logger.debug("Registering {} as {} in application context.", bean.getClass().getSimpleName(), bean);
		springContext.getBeanFactory().registerSingleton(bean.getClass().getSimpleName(), bean);
	}

	public void finishConfiguration() throws FailedComputationSetupException {
		checkState(!configured, "Task is already configured.");

		try {
			springContext.refresh();
			configured = true;
		} catch (final BeansException e) {
			logger.error("Cannot refresh the Spring context", e);
			throw new FailedComputationSetupException("Cannot refresh the Spring context", e);
		}
	}

	public Task buildAndSchedule(final ListeningScheduledExecutorService executorService,
	                             final FutureCallback<Object> executionListener)
		throws FailedComputationSetupException {
		requireNonNull(executorService);
		requireNonNull(executionListener);
		checkState(configured, "Task is not configured.");

		try {
			final Runnable runnable = (Runnable)springContext.getBean("runnable");
			final String className = springContext.getType("runnable").getCanonicalName();
			if (className == null) {
				throw new FailedComputationSetupException("Provided class has no name.");
			}

			logger.info("Starting execution of {}", runnable);
			final ListenableScheduledFuture<?> future = executorService.schedule(withThreadName("COMPUTE", runnable),
			                                                                     0L, TimeUnit.SECONDS);
			Futures.addCallback(future, executionListener);
			if (runnable instanceof Pauseable) {
				return new PauseableStartedTask(className, (Pauseable)runnable, future);
			}
			return new StartedTask(className, runnable, future);
		} catch (final BeansException e) {
			logger.error("Cannot get runnable from the context", e);
			throw new FailedComputationSetupException("Cannot get runnable from the context.", e);
		}
	}

	@Override public String toString() {
		return toStringHelper(this).add("context", springContext).add("configured", configured).toString();
	}

}
