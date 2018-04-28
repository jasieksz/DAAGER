# AgE 3

AgE 3 is a new version and complete rewrite of the distributed agent-based computational platform [AgE](https://www.age.agh.edu.pl/).

**This is beta software** and is still under heavy development.
Although this version is released, it is only a *snapshot* of a current development branch and there is no guarantee that future version will provide the same functionality or API.

[![pipeline status](https://gitlab.com/age-agh/age3/badges/develop/pipeline.svg)](https://gitlab.com/age-agh/age3/commits/develop)

## Running AgE 3

You can find info on how to run AgE 3 in the [documentation](https://docs.age.agh.edu.pl/age3/user/running.html) or, alternatively, in [this repo](docs/user/running.md).

## Compute modules

*Compute modules* are implementations of various algorithms that can be run using AgE.

Currently, AgE provides following modules:
-  **Evolutionary Algorithms** – classical evolutionary algorithms implemented in streaming (data-flow) fashion.
-  **Stream Agents** – multi-agent system based on streaming (data-flow) implementation.
   This also contains EMAS (Evolutionary Multi-Agent System) implementation.

   Documentation for this module is available in `docs/user/stream-agents` directory and on <https://docs.age.agh.edu.pl/age3/user/stream-agents/>.
-  **LABS** (Low Autocorrelation Binary Sequences) – LABS-related operators for Stream Agents.

   Documentation for this module is available in `docs/user/ogr-labs` directory and on <https://docs.age.agh.edu.pl/age3/user/ogr-labs/>.
-  **OGR** (Optimal Golomb Ruler) – OGR-related operators for Stream Agents.

   Documentation for this module is available in `docs/user/ogr-labs` directory and on <https://docs.age.agh.edu.pl/age3/user/ogr-labs/>.

## More documentation

For more documentation see the `docs/` directory in this repo or check the HTML version on <https://docs.age.agh.edu.pl/age3/>.

## Structure of this repository

Currently, this repository contains four Gradle modules (besides root):

- **age3-compute-api** – API for compute modules,
- **age3-console** – implementation of the console,
- **age3-core** – implementation of the computational node,
- **age3-examples** – compute examples.

And the following compute modules:
- **age3-stream-agents** – stream-processing-based multi-agent-system,
- **compute-ea** – (plain) Evolutionary Algorithms,
- **compute-labs** – Low Autocorrelation Binary Sequences,
- **compute-ogr** – Optimal Golomb Ruler.

Other directories include:

- `.ide` – suggested configuration for your IDEs (IntelliJ IDEA and Eclipse),
- `docs` – documentation (available also at <https://docs.age.agh.edu.pl/age3/>),
- `docker` – files required to build Docker images,
- `gradle` – Gradle wrapper.

## Links

* [Main page for AgE](https://age.agh.edu.pl/)
* [Repository on GitLab](https://gitlab.com/age-agh/age3)
* [Maven Repository](https://repo.age.agh.edu.pl/repository/maven-public/)
* [Documentation](https://docs.age.agh.edu.pl/age3/)
* [Javadocs](https://docs.age.agh.edu.pl/javadocs/age3/develop/)
