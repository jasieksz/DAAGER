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
package pl.edu.agh.age.examples;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example shows how the minimal runnable should be configured.
 *
 * There is a Spring configuration file: `pl/edu/agh/age/examples/spring-simple.xml` that demonstrates how the basic
 * configuration should looks like if it is required.
 */
public final class Simple implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Simple.class);

	@Override public void run() {
		logger.info("This is the simplest possible example of a computation.");
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
