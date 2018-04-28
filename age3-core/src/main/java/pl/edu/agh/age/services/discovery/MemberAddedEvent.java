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

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.services.identity.NodeType;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public final class MemberAddedEvent implements DiscoveryEvent {

	private final String memberId;

	private final NodeType memberType;

	private final LocalDateTime timestamp = LocalDateTime.now();

	public MemberAddedEvent(final String memberId, final NodeType memberType) {
		this.memberId = requireNonNull(memberId);
		this.memberType = requireNonNull(memberType);
	}

	public String memberId() {
		return memberId;
	}

	public NodeType memberType() {
		return memberType;
	}

	@Override public int hashCode() {
		return Objects.hash(memberId, memberType, timestamp);
	}

	@Override public boolean equals(final @Nullable Object obj) {
		if (!(obj instanceof MemberAddedEvent)) {
			return false;
		}
		final MemberAddedEvent other = (MemberAddedEvent)obj;

		return Objects.equals(memberId, other.memberId)
		       && Objects.equals(memberType, other.memberType)
		       && Objects.equals(timestamp, other.timestamp);
	}

	@Override public String toString() {
		return toStringHelper(this).add("id", memberId).add("type", memberType).toString();
	}
}
