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

import java.io.Serializable;
import java.util.Set;

/**
 * Wraps cached node identity information in a single, serializable object.
 */
public interface NodeDescriptor extends Serializable {

	/**
	 * Returns the ID of the node described in this descriptor.
	 */
	String id();

	/**
	 * Returns the type of the node described in this descriptor.
	 */
	NodeType type();

	/**
	 * Returns the set of services running on the node described by this descriptor.
	 */
	Set<String> services();

}
