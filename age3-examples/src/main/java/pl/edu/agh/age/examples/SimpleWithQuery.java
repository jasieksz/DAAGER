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
import static java.util.stream.Collectors.toList;

import pl.edu.agh.age.compute.api.QueryProcessor;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * The simplest possible computation that uses queries.
 */
public final class SimpleWithQuery implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SimpleWithQuery.class);

	@Inject private @MonotonicNonNull QueryProcessor<SampleCached> queryProcessor;

	@Override public void run() {
		log.info("This is the simplest possible example of a computation.");
		log.info("Query processor: {}.", queryProcessor);

		queryProcessor.schedule(() -> new SampleCached());

		for (int i = 0; i < 100; i++) {
			log.info("Iteration {}.", i);

			final List<SampleCached> collect = queryProcessor.query().collect(toList());
			log.info("Results: {}.", collect);

			try {
				TimeUnit.SECONDS.sleep(1L);
			} catch (final InterruptedException e) {
				log.debug("Interrupted.", e);
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	@Override public String toString() {
		return toStringHelper(this).toString();
	}

	public static final class SampleCached implements Serializable {

		private static final long serialVersionUID = 728385759168736437L;

		private final long random = new Random().nextLong();

		private final LocalDateTime createdAt = LocalDateTime.now();

		public long random() {
			return random;
		}

		@Override public String toString() {
			return toStringHelper(this).add("createdAt", createdAt).add("random", random).toString();
		}
	}
}