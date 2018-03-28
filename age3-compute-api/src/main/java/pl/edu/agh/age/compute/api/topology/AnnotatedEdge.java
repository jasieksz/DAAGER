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

package pl.edu.agh.age.compute.api.topology;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableSet;

import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

/**
 * An edge with a set of string annotations attached to it.
 */
public final class AnnotatedEdge extends DefaultEdge {

	private static final long serialVersionUID = 52204165650747503L;

	private final Set<String> annotations;

	/**
	 * Creates an empty edge.
	 */
	public AnnotatedEdge() {
		this(ImmutableSet.of());
	}

	/**
	 * Creates an edge with provided annotations.
	 */
	public AnnotatedEdge(final String... annotations) {
		this(ImmutableSet.copyOf(annotations));
	}

	AnnotatedEdge(final Set<String> annotations) {
		this.annotations = ImmutableSet.copyOf(requireNonNull(annotations));
	}

	public Set<String> annotations() {
		return annotations;
	}

	@Override public String toString() {
		return String.format("(%s : %s)%s", getSource(), getTarget(), annotations.toString());
	}

	@Override public AnnotatedEdge clone() {
		return (AnnotatedEdge)super.clone();
	}
}
