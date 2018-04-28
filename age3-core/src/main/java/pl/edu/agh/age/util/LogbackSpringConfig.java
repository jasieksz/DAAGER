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

package pl.edu.agh.age.util;

import pl.edu.agh.age.services.identity.NodeIdentityService;

import com.hazelcast.core.HazelcastInstance;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.ext.spring.ApplicationContextHolder;

@Configuration
public class LogbackSpringConfig {

	@Bean @Lazy public static ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}

	@Bean @Lazy public static LoggerContext loggerContext() {
		return (LoggerContext)LoggerFactory.getILoggerFactory();
	}

	@Bean(initMethod = "start", destroyMethod = "stop") @Lazy
	public static HazelcastAppender hazelcastAppender(final LoggerContext ctx,
	                                                  final HazelcastInstance hazelcastInstance,
	                                                  final NodeIdentityService identityService) {
		final HazelcastAppender appender = new HazelcastAppender(hazelcastInstance, identityService);
		appender.setContext(ctx);
		return appender;
	}
}
