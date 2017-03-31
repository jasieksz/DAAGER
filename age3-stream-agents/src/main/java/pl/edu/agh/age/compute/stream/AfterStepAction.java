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

package pl.edu.agh.age.compute.stream;

import javaslang.Function3;
import javaslang.collection.List;
import javaslang.collection.Map;

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
 * @param <T>
 * 		type of agents
 * @param <K>
 * 		type of keys in the returned map
 */
@FunctionalInterface
public interface AfterStepAction<T extends Agent, K> extends Function3<Long, Long, List<T>, Map<K, Object>> {

}
