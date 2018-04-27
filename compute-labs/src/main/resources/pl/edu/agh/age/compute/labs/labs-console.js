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
