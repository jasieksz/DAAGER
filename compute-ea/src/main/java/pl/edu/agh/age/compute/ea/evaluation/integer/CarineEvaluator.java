/*
 * Copyright (C) 2006-2018 Intelligent Information Systems Group.
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

package pl.edu.agh.age.compute.ea.evaluation.integer;

import pl.edu.agh.age.compute.ea.evaluation.Evaluator;
import pl.edu.agh.age.compute.ea.solution.IntegerVectorSolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.vavr.collection.Seq;

/**
 * CarineEvaluator is used for running a step of calculation with carine application. Carine homepage:
 * http://www.atpcarine.com/
 */
public final class CarineEvaluator implements Evaluator<IntegerVectorSolution> {

	private static final int MAX_EVALUATION_TIME = 99999;

	private static final int MAX_EXECUTION_TIME = 3;

	private static final Logger logger = LoggerFactory.getLogger(CarineEvaluator.class);

	private final String carinePath;

	private final String geoPath;

	public CarineEvaluator(final String carinePath, final String geoPath) {
		this.carinePath = carinePath;
		this.geoPath = geoPath;
	}

	public CarineEvaluator() {
		this("carine/carine", "carine/geo001-1.t");
	}

	@Override public double evaluate(final IntegerVectorSolution solution) {
		final String[] commandString = createCommandString(solution.unwrap());
		logger.debug("Command string is: {}", Arrays.toString(commandString));

		final long startTime = System.currentTimeMillis();
		final boolean isSuccess = executeCommandString(commandString);

		long timeElapsed = System.currentTimeMillis() - startTime;
		if (!isSuccess || (timeElapsed > MAX_EVALUATION_TIME)) {
			timeElapsed = MAX_EVALUATION_TIME;
		}
		logger.debug("Time elapsed: {}", timeElapsed);

		return (double)-timeElapsed;
	}

	private String[] createCommandString(final Seq<Integer> representation) {
		// we are in target/classes directory, where files from resources are copied
		final String[] cmd = new String[11];
		cmd[0] = carinePath;
		cmd[1] = geoPath;
		cmd[2] = "xo=off";
		cmd[3] = "wt=" + MAX_EXECUTION_TIME; // max execution time
		cmd[4] = "id=" + representation.get(0); // 0-1
		cmd[5] = "md=" + representation.get(1); // 0-30
		cmd[6] = "uct=" + representation.get(2); // 1-50000
		cmd[7] = "mtl=" + representation.get(3); // 0-255
		cmd[8] = "ml=" + representation.get(4); // 1-48
		cmd[9] = "mtu=" + representation.get(5); // 1-64
		cmd[10] = "mtdu=" + representation.get(6); // 1-63
		return cmd;
	}

	@SuppressWarnings("UseOfProcessBuilder") private boolean executeCommandString(final String[] commandString) {
		Process process = null;
		try {
			process = new ProcessBuilder(commandString).redirectErrorStream(true).start();
			try (final BufferedReader input = new BufferedReader(
				new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
				String line;
				while ((line = input.readLine()) != null) {
					if (line.contains("PROOF FOUND!")) {
						return true;
					}
				}
			}
		} catch (final IOException e) {
			logger.error("An error happened when executing command string: {}", commandString, e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}
}
