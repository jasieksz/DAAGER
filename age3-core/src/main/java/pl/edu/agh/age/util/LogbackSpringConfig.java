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
