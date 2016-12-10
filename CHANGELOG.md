# AgE 3 Changelog

## 0.4 -- TBA

- Add the batch mode to the console (#25).
- `pl.edu.agh.age.console.command.ComputationCommand.info` provides now real info about computation (#26).
- Compute setup failures do not result in node failure anymore (#24).

## 0.3.1 -- TBA

- Fix broken `pl.edu.agh.age.compute.stream.emas.Pipeline.selectPairs` – now correctly prevents repetitions.
- Remove assumption that `pl.edu.agh.age.compute.stream.AgentsRegistry.getBestAgentEvaluation` is always present
  during logging.
- Bump some logging events to higher level in `DefaultLoggingService`.

## 0.3 -- 28.11.2016

Initial public version.
