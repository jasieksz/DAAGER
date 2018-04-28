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

package pl.edu.agh.age.compute.ea.evaluation.binary;


import static org.assertj.core.api.Assertions.assertThat;

import pl.edu.agh.age.compute.ea.solution.BooleanVectorSolution;

import org.junit.Before;
import org.junit.Test;

/*
 * Created: 2017-06-07.
 */
public class CountEvaluatorTest {

	private CountEvaluator evaluator;

	@Before public void setUp() {
		evaluator = new CountEvaluator();
	}

	@Test public void evaluate() {
		final BooleanVectorSolution solution = new BooleanVectorSolution(new boolean[] {true, false, true, false});

		assertThat(evaluator.evaluate(solution)).isEqualTo(2.0);
	}

}
