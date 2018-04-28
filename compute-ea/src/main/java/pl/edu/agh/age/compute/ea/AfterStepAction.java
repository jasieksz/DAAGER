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

package pl.edu.agh.age.compute.ea;

import pl.edu.agh.age.compute.ea.solution.Solution;

import io.vavr.Function3;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * Interface for functions executed after the step execution in a workplace.
 *
 * The parameters of the function are:
 * - id of the workplace (as Long),
 * - step number (as Long),
 * - current population (as {@link List}).
 *
 * The function should return statistics map for the current step.
 *
 * @param <S>
 * 		type of a solution
 * @param <K>
 * 		type of keys in the returned map
 */
@FunctionalInterface
public interface AfterStepAction<S extends Solution<?>, K> extends Function3<Long, Long, List<S>, Map<K, Object>> {

}