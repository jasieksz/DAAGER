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

package pl.edu.agh.age.services.identity.internal;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.identity.NodeType;

import com.google.common.collect.ImmutableSet;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * Default node descriptor.
 */
public final class NodeDescriptor implements pl.edu.agh.age.services.identity.NodeDescriptor {

	private static final long serialVersionUID = -4461499899468219523L;

	private final String id;

	private final NodeType type;

	private final ImmutableSet<String> services;

	public NodeDescriptor(final String id, final NodeType type, final Set<String> services) {
		this.id = requireNonNull(id);
		this.type = requireNonNull(type);
		this.services = ImmutableSet.copyOf(requireNonNull(services));
	}

	@Override public Set<String> services() {
		return services;
	}

	@Override public String id() {
		return id;
	}

	@Override public NodeType type() {
		return type;
	}

	@Override public int hashCode() {
		return Objects.hash(id, type);
	}

	@Override public boolean equals(final @Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NodeDescriptor)) {
			return false;
		}
		final NodeDescriptor other = (NodeDescriptor)obj;
		return Objects.equals(id, other.id) && Objects.equals(type, other.type);
	}

	@Override public String toString() {
		return toStringHelper(this).addValue(id).add("type", type).toString();
	}
}
