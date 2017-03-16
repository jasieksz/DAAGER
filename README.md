# AgE 3

AgE 3 is a new version and complete rewrite of the distributed agent-based computational platform
[AgE](https://www.age.agh.edu.pl/).

**This is beta software** and is still under heavy development. Although this version is released,
it is only a *snapshot* of a current development branch and
there is no guarantee that future version will provide the same functionality or API.

## Simple example

To run a very simple example from the console, execute:
```bash
./gradlew --no-daemon age3-examples:standalone
```

This will start AgE 3 in a standalone mode and present you a console prompt:
```
AgE>
```

Then, execute the `Simple` example:
```js
test.executeExample({ example:'Simple' })
```

After a moment, you should notice an output on the console similar to this line:
```
I pl.edu.agh.age.examples.Simple           : This is the simplest possible example of a computation.
```

To exit the console, press `Ctrl`-`d` or type `quit()`. This will terminate the standalone AgE node.

> **Note:** `--no-daemon` is required to disable usage of the Gradle daemon.
> It would prevent console from accepting keyboard input.

### Other examples

Other examples can be also run this way – to display their list, use `test.listExamples()` command.

Some examples are supposed to be run in a distributed environment.
To do so, start at least two nodes with the proper classpath and proper configuration.
You can use Gradle task located in `age3-examples` module:
```bash
./gradlew age3-examples:node
```

Then, start the shell:
```bash
./gradlew --no-daemon age3-examples:shell
```

## Running AgE 3

AgE can be run in three modes:

- computational, non-interactive node (part of the distributed cluster) — `age3-core` module,
- console-only for accessing the cluster — `age3-console` module,
- standalone single node with console — `age3-console` module.

Each of these can be run from the Gradle configuration, from the distribution jar or from the IDE.

### Using Gradle

To run the node:
```bash
./gradlew age3-core:node
```

To run the console:
```bash
./gradlew --no-daemon age3-console:shell
```

> **Note:** console requires that at least one node is run and accessible.

To run the standalone node with the console:
```bash
./gradlew --no-daemon age3-console:standalone
```

> **Note:** both the non-interactive node and the standalone modes are not really useful when run from the `age3-console` package.
> Computation requires that all used classes are available from the classpath, so you would need to change the classpath for these Gradle tasks.

To pass arguments to these Gradle tasks, you can use the `appArgs` property, for example:
```bash
cd age3-stream-agents
../gradlew age3-core:node -PappArgs="['classpath:pl/edu/agh/age/compute/stream/example/spring-stream-example.xml']"
```

### Using distribution jar

Firstly, you need to build the distribution jar and startup scripts:
```bash
./gradlew age3-console:distShadowTar
```
This will create a tar file in the `age3-console/build/distributions/` directory.
`cd` into it, unpack the tarball, and then run the following command to start the console:
```bash
age3-console/bin/age3-console
```
Or, for starting the standalone node:
```bash
age3-console/bin/age3-console standalone
```

In a similar way, you can build and start the non-interactive node.
```bash
./gradlew age3-core:distShadowTar
cd age3-core/build/distributions/
tar -xvf age3-core.tar
./gradlew age3-core:node
```

## Stream Agents

The **age3-stream-agents** module contains a modern multi-agent system implementation with stream processing.
To run an example from Gradle one needs to run `node` task with path to Spring configuration file, as follows:
```bash
./gradlew age3-stream-agents:node -PappArgs="['classpath:pl/edu/agh/age/compute/stream/example/spring-stream-example.xml']"
```

## More documentation

For more documentation see the `docs/` directory in this repo or check the HTML version on <https://docs.age.agh.edu.pl/age3/>.

## Structure of this repository

Currently, this repository contains four Gradle modules (besides root):

- **age3-console** – implementation of the console,
- **age3-core** – implementation of the computational node,
- **age3-examples** – examples,
- **age3-stream-agents** – stream-processing-based multi-agent-system.

Other directories include:

- `.ide` – suggested configuration for your IDEs (IntelliJ IDEA and Eclipse),
- `checkerframework` – files needed by Checker Framework,
- `docs` – documentation (available also at <https://docs.age.agh.edu.pl/age3/>),
- `docker` – files required to build Docker images,
- `gradle` – Gradle wrapper.

## Links

* [Repository on GitLab](https://gitlab.com/age-agh/age3)
* [Maven Repository](https://repo.age.agh.edu.pl/repository/maven-public/)
* [Documentation](https://docs.age.agh.edu.pl/age3/)
* [Javadocs](https://docs.age.agh.edu.pl/javadocs/age3/develop/)
