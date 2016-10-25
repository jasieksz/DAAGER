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

import java.util.concurrent.TimeUnit;

/**
 * This example shows the long running version of the minimal example that throws an exception in the end. Used mainly
 * for testing.
 */
public final class SimpleLongRunningWithError implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SimpleLongRunningWithError.class);

	@SuppressWarnings("ProhibitedExceptionThrown") @Override public void run() {
		logger.info("This is the simplest possible example of a computation.");
		for (int i = 0; i < 10; i++) {
			logger.info("Iteration {}.", i);

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (final InterruptedException e) {
				logger.debug("Interrupted.", e);
				Thread.currentThread().interrupt();
				return;
			}
		}

		// This example is testing computational errors so we throw an error
		throw new RuntimeException("Some computation error");
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}
}
