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

import com.google.common.base.MoreObjects;

import java.io.Serializable;

final class MigrationMessage implements Serializable {

	private static final long serialVersionUID = 6074663729329120943L;

	final long targetWorkplace;

	final Agent agent;

	MigrationMessage(final long targetWorkplace, final Agent agent) {
		assert (targetWorkplace >= 0) && (agent != null);

		this.targetWorkplace = targetWorkplace;
		this.agent = agent;
	}

	@Override public String toString() {
		return MoreObjects.toStringHelper(this).add("agent", agent).toString();
	}
}
