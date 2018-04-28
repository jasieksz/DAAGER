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

package pl.edu.agh.age.services.discovery;

import pl.edu.agh.age.services.identity.NodeDescriptor;

import java.util.Optional;
import java.util.Set;

/**
 * Discovery service is responsible for collecting info about other nodes in the cluster and providing it to other
 * services.
 */
public interface DiscoveryService {
	/**
	 * Returns {@link NodeDescriptor}s of cluster members matching the given criteria.
	 *
	 * @param criteria
	 * 		Criteria in the form of SQL query.
	 *
	 * @return A set of node descriptors.
	 *
	 * @see com.hazelcast.query.SqlPredicate
	 */
	Set<NodeDescriptor> membersMatching(String criteria);

	/**
	 * Returns {@link NodeDescriptor}s of all cluster members.
	 *
	 * @return A set of node descriptors.
	 */
	Set<NodeDescriptor> allMembers();

	/**
	 * Returns {@link NodeDescriptor} for the node with the given ID.
	 *
	 * @param id
	 * 		an ID to look up.
	 *
	 * @return a matching NodeDescriptor or empty Optional if there is no such member.
	 */
	Optional<NodeDescriptor> memberWithId(String id);
}
