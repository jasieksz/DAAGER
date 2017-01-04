# AgE 3 Changelog

## 0.4 -- TBA

Console:
- Add the batch mode to the console (#25).
- `pl.edu.agh.age.console.command.ComputationCommand.info` provides now real info about computation (#26).
- Add `computation.waitUntilFinished` command (#34).

Core:
- Compute setup failures do not result in node failure anymore (#24).
- Make `HazelcastAppender` configurable and disable it by default (#32).

General compute:
- Introduce topology API (`pl.edu.agh.age.compute.api.TopologyProvider`) for compute-level applications
  and use it in StreamAgents migrations (#30).
- Full clean up is performed on all utilities during task clean-up (#34).

Stream Agents:
- Use topology API in migrations (#30).
- Make `Improvement` run after evaluation of the solution when configured using `SexualReproductionBuilder` (#33).
- Fix situations when the main runnable did not wait for workplaces (#29). 

## 0.3.1 -- TBA

- Fix broken `pl.edu.agh.age.compute.stream.emas.Pipeline.selectPairs` – now correctly prevents repetitions.
- Remove assumption that `pl.edu.agh.age.compute.stream.AgentsRegistry.getBestAgentEvaluation` is always present
  during logging.
- Bump some logging events to higher level in `DefaultLoggingService`.

## 0.3 -- 28.11.2016

Initial public version.
