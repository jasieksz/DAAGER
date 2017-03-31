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

package pl.edu.agh.age.compute.stream.example;

import pl.edu.agh.age.compute.stream.Environment;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.emas.EmasAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaslang.collection.List;

/**
 * Sample step to test that topology of workplaces works.
 */
public final class SampleTopologyStep implements Step<EmasAgent> {

	private static final Logger logger = LoggerFactory.getLogger(SampleTopologyStep.class);

	@Override public List<EmasAgent> stepOn(final long stepNumber, final List<EmasAgent> population,
	                                        final Environment environment) {
		logger.info("Neighbours of {} are {}", environment.workplaceId(), environment.neighbours());
		return population;
	}
}
