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

package pl.edu.agh.age.runnables;

import pl.edu.agh.age.compute.api.ThreadPool;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@SuppressWarnings("unused")
public final class SimpleTestWithThreads implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SimpleTestWithThreads.class);

	private final ThreadPool threadPool;

	@Inject public SimpleTestWithThreads(final ThreadPool threadPool) {
		this.threadPool = threadPool;
	}

	@Override public void run() {
		logger.info("Running");
		final List<ListenableFuture<?>> futures = threadPool.submitAll(ImmutableList.of(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					TimeUnit.SECONDS.sleep(1);
					logger.info("Still running in thread");
				}
			} catch (final InterruptedException e) {
				logger.info("Interrupted exception", e);
			}
			logger.info("After finish");
		}));
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (final InterruptedException e) {
			logger.info("Interrupted exception", e);
		}
		futures.forEach(f -> f.cancel(true));
		logger.info("Finished!");
	}

}
