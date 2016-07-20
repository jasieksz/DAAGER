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

package pl.edu.agh.age.services.worker.internal;

import static java.util.stream.Collectors.toList;

import pl.edu.agh.age.compute.api.ThreadPool;
import pl.edu.agh.age.util.Runnables;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.Executors;

public final class DefaultThreadPool implements ThreadPool {

	private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

	@Override public List<ListenableFuture<?>> submitAll(final List<? extends Runnable> runnables) {
		return runnables.stream().map(Runnables::swallowingRunnable).map(service::submit).collect(toList());
	}

	void shutdownAll() {
		service.shutdownNow();
	}
}
