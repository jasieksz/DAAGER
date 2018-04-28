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

/***************************************************************/

var ROOT_DIRECTORY = "src/main/resources/pl/edu/agh/age/labs/";
var COMPUTATION_REPETITIONS = 3;
var PROPERTIES = ["labs-config.properties"];
var IMPROVEMENT_PROPERTIES = [];
var ITERATIVE_IMPROVEMENT_PROPERTIES = ["labs-config.properties"];
var ITERATIVE_CACHED_IMPROVEMENT_PROPERTIES = [];

/***************************************************************/

var ROOT_CONFIG_XML = "labs-config.xml";
var ROOT_CONFIG_IMPROVEMENT_XML = "labs-config-improvement.xml";
var ROOT_CONFIG_ITERATIVE_IMPROVEMENT_XML = "labs-config-iterative-improvement.xml";
var ROOT_CONFIG_ITERATIVE_CACHED_IMPROVEMENT_XML = "labs-config-iterative-cached-improvement.xml";

load("src/main/resources/console-script.js");

runComputations(ROOT_DIRECTORY, ROOT_CONFIG_XML, PROPERTIES, COMPUTATION_REPETITIONS);
runComputations(ROOT_DIRECTORY, ROOT_CONFIG_IMPROVEMENT_XML, IMPROVEMENT_PROPERTIES, COMPUTATION_REPETITIONS);
runComputations(ROOT_DIRECTORY, ROOT_CONFIG_ITERATIVE_IMPROVEMENT_XML, ITERATIVE_IMPROVEMENT_PROPERTIES,
                COMPUTATION_REPETITIONS);
runComputations(ROOT_DIRECTORY, ROOT_CONFIG_ITERATIVE_CACHED_IMPROVEMENT_XML, ITERATIVE_CACHED_IMPROVEMENT_PROPERTIES,
                COMPUTATION_REPETITIONS);
