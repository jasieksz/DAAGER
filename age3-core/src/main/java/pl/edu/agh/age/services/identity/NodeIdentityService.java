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

package pl.edu.agh.age.services.identity;

import java.util.Set;

/**
 * Node identity service is a local service that provides retrospection about the node and defines its identity.
 */
public interface NodeIdentityService {
	/**
	 * Returns the stringified ID of the node.
	 */
	String nodeId();

	/**
	 * Returns the descriptor for the node that contains cached, serializable information from the service.
	 */
	NodeDescriptor descriptor();

	/**
	 * Returns the set of running services.
	 */
	Set<String> services();

	/**
	 * Tells whether the node is compute node.
	 */
	boolean isCompute();

	/**
	 * Checks whether node has a given type.
	 *
	 * @param type
	 * 		a type to check.
	 */
	boolean is(NodeType type);
}
