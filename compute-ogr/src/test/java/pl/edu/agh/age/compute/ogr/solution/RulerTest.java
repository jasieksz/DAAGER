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

package pl.edu.agh.age.compute.ogr.solution;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public final class RulerTest {

	private static final int[] ONE_MARK_RULER_DIRECT = {0};

	private static final int[] ONE_MARK_RULER_INDIRECT = {};

	private static final int[] TWO_MARK_RULER_DIRECT = {0, 1};

	private static final int[] TWO_MARK_RULER_INDIRECT = {1};

	private static final int[] THREE_MARK_RULER_DIRECT = {0, 1, 3};

	private static final int[] THREE_MARK_RULER_INDIRECT = {1, 2};

	private static final int[] FOUR_MARK_RULER_DIRECT = {0, 1, 4, 6};

	private static final int[] FOUR_MARK_RULER_INDIRECT = {1, 3, 2};

	private static final int[] FIVE_MARK_RULER_DIRECT = {0, 1, 4, 9, 11};

	private static final int[] FIVE_MARK_RULER_INDIRECT = {1, 3, 5, 2};

	@Test public void testDirectToIndirect() {
		testDirectToIndirectRuler(ONE_MARK_RULER_DIRECT, ONE_MARK_RULER_INDIRECT);
		testDirectToIndirectRuler(TWO_MARK_RULER_DIRECT, TWO_MARK_RULER_INDIRECT);
		testDirectToIndirectRuler(THREE_MARK_RULER_DIRECT, THREE_MARK_RULER_INDIRECT);
		testDirectToIndirectRuler(FOUR_MARK_RULER_DIRECT, FOUR_MARK_RULER_INDIRECT);
		testDirectToIndirectRuler(FIVE_MARK_RULER_DIRECT, FIVE_MARK_RULER_INDIRECT);
	}

	@Test public void testIndirectToDirect() {
		testIndirectToDirectRuler(ONE_MARK_RULER_DIRECT, ONE_MARK_RULER_INDIRECT);
		testIndirectToDirectRuler(TWO_MARK_RULER_DIRECT, TWO_MARK_RULER_INDIRECT);
		testIndirectToDirectRuler(THREE_MARK_RULER_DIRECT, THREE_MARK_RULER_INDIRECT);
		testIndirectToDirectRuler(FOUR_MARK_RULER_DIRECT, FOUR_MARK_RULER_INDIRECT);
		testIndirectToDirectRuler(FIVE_MARK_RULER_DIRECT, FIVE_MARK_RULER_INDIRECT);
	}

	private void testDirectToIndirectRuler(final int[] directRepresentation, final int[] indirectRepresentation) {
		// Given & When
		final Ruler ruler = new Ruler(directRepresentation, true);

		// Then
		assertRuler(ruler, directRepresentation, indirectRepresentation);
	}

	private void testIndirectToDirectRuler(final int[] directRepresentation, final int[] indirectRepresentation) {
		// Given & When
		final Ruler ruler = new Ruler(indirectRepresentation, false);

		// Then
		assertRuler(ruler, directRepresentation, indirectRepresentation);
	}

	private static void assertRuler(final Ruler ruler, final int[] expectedDirect, final int[] expectedIndirect) {
		assertThat(ruler.directRepresentation()).isEqualTo(expectedDirect);
		assertThat(ruler.indirectRepresentation()).isEqualTo(expectedIndirect);
	}

}
