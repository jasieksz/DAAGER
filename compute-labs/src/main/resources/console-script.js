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

var FILE_PREFIX = "file:";

function runComputations(rootDirectory, configFileName, propertiesFileNames, repetitions) {
	var configFile = FILE_PREFIX + rootDirectory + configFileName;
	for (var i = 0; i < propertiesFileNames.length; i++) {
		var propertiesFile = FILE_PREFIX + rootDirectory + propertiesFileNames[i];
		for (var j = 0; j < repetitions; j++) {
			streamcomputation.reloadLogger();
			streamcomputation.load({config: configFile, propertiesFiles: [propertiesFile]});
			streamcomputation.start();
			sleep(1000); // wait for initialization to finish
			streamcomputation.waitUntilFinished();
			streamcomputation.clean();
		}
	}
}
