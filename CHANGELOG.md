# AgE 3 Changelog

## 0.5 -- TBD

Stream Agents:
- Broad changes to Stream Agents (#38):
  - Probabilistic comparators for EMAS Agents with probability proportional to the agent's quality
  - Proportional energy transfer modified to match old version (only parent's loss is defined)
  - Constant energy transfer was introduced
  - AsexualReproduction was added
  - Evaluation and improvement are separated from reproduction
  - Workplace generator added to configuration
  - Step has been extended with the step number parameter
  - Logger improvements

## 0.4 -- 04.04.2017

Console:
- Add the batch mode to the console (#25).
- `pl.edu.agh.age.console.command.ComputationCommand.info` provides now real info about computation (#26).
- Add `computation.waitUntilFinished` command (#34).

Core:
- Compute setup failures do not result in node failure anymore (#24).
- Make `HazelcastAppender` configurable and disable it by default (#32).
- Make configuration (core) more flexible for the user (#35):
  - allow overriding Hazelcast config,
  - add possibility to specify cluster address for the console,
  - remove the need for specifying explicit location of the logback configuration.

General compute:
- Introduce topology API (`pl.edu.agh.age.compute.api.TopologyProvider`) for compute-level applications
  and use it in StreamAgents migrations (#30).
- Full clean up is performed on all utilities during task clean-up (#34).
- Configuration using `properties` files is now possible (#36):
  properties for the compute modules can be loaded both in batch mode and in the console. 

Stream Agents:
- Use topology API in migrations (#30).
- Make `Improvement` run after evaluation of the solution when configured using `SexualReproductionBuilder` (#33).
- Fix situations when the main runnable did not wait for workplaces (#29). 

Other:
- Miscellaneous documentation and configuration fixes.

## 0.3.1 -- 04.04.2017

- Fix broken `pl.edu.agh.age.compute.stream.emas.Pipeline.selectPairs` – now correctly prevents repetitions.
- Remove assumption that `pl.edu.agh.age.compute.stream.AgentsRegistry.getBestAgentEvaluation` is always present
  during logging.
- Bump some logging events to higher level in `DefaultLoggingService`.

## 0.3 -- 28.11.2016

Initial public version.
