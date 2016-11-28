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

package pl.edu.agh.age.compute.stream

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.ext.spring.DelegatingLogbackAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.INFO

def bySecond = timestamp("yyyyMMdd'T'HHmmss")

appender("STREAM-FILE", FileAppender) {
	file = "logs/emas-${bySecond}.log"
	append = false
	encoder(PatternLayoutEncoder) {
		pattern = "%msg%n"
	}
}

appender("FILE", FileAppender) {
	file = "node-${bySecond}.log"
	append = false
	encoder(PatternLayoutEncoder) {
		pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{40} - %msg%n"
	}
}

appender("CONSOLE", ConsoleAppender) {
	filter(ch.qos.logback.classic.filter.ThresholdFilter) {
		level = INFO
	}
	encoder(PatternLayoutEncoder) {
		pattern = "%highlight(%.-1level) %green(%-40logger{39}) : %msg%n"
	}
}

appender("hazelcastAppender", DelegatingLogbackAppender) {
}

logger("stream", DEBUG, ["STREAM-FILE"], additivity = false)

root(ALL, ["FILE", "hazelcastAppender"])
logger("pl.edu.agh.age", DEBUG, ["CONSOLE"])
logger("com.hazelcast", INFO)
logger("org.springframework", INFO)
