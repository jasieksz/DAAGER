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

package pl.edu.agh.age.compute.ea.preselection;

import pl.edu.agh.age.compute.ea.solution.Solution;

import java.io.Serializable;

import io.vavr.Function1;
import io.vavr.collection.List;

/**
 * Preselection strategy interface.
 *
 * @param <S>
 * 		The type of {@link ISolution} to be preselected.
 * @param <E>
 * 		The type of evaluations
 */
@FunctionalInterface
public interface Preselection<T extends Solution<? extends Serializable>> extends Function1<List<T>, List<T>> {
	List<T> preselect(List<T> population);

	@Override default List<T> apply(final List<T> population) {
		return preselect(population);
	}
}
