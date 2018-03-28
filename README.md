# AgE 3

AgE 3 is a new version and complete rewrite of the distributed agent-based computational platform [AgE](https://www.age.agh.edu.pl/).

**This is beta software** and is still under heavy development. 
Although this version is released, it is only a *snapshot* of a current development branch and there is no guarantee that future version will provide the same functionality or API.

[![pipeline status](https://gitlab.com/age-agh/age3/badges/develop/pipeline.svg)](https://gitlab.com/age-agh/age3/commits/develop)

## Simple example

To run a very simple example from the console, execute:

```bash
./gradlew age3-examples:build
```

The copy the output JAR to `lib/` directory:

```bash
mkdir lib
cp age3-examples/build/libs/age3-examples-0.6-SNAPSHOT.jar lib/
```

Then start the console in the **standalone** mode:

```bash
./gradlew --no-daemon --console=plain age3-console:standalone
```

This will start the AgE 3 node and present you a console prompt:

```
AgE>
```

Then, execute the `Simple` example:
```js
test.executeExample({ example:'Simple' })
```

After a moment, you should notice an output in the log file similar to this line:

```
00:26:04.867 [COMPUTE] INFO  pl.edu.agh.age.examples.Simple - This is the simplest possible example of a computation.
```

To exit the console, press `Ctrl`-`d` or type `quit()`. This will terminate the standalone AgE node.

> **Note:** `--no-daemon` is required to disable usage of the Gradle daemon.
> It would prevent console from accepting keyboard input.

You can also build the executable shadow JAR of console:

```bash
./gradlew age3-console:shadowJar
```

And then run it:

```bash
java -jar age3-console/build/libs/age3-console.jar
```

### Other examples

Other examples can be also run this way – to display their list, use `test.listExamples()` command.

Some examples are supposed to be run in a distributed environment.
To do so, start at least two nodes using the following command:

```bash
./gradlew age3-core:node
```

Then, start the shell but **not** in the standalone mode:
```bash
./gradlew --no-daemon --console=plain age3-examples:shell
```

And you can now execute other examples.

## Running AgE 3

You can find info on how to run AgE 3 in the [documentation](https://docs.age.agh.edu.pl/age3/user/running.html) or, alternatively, in [this repo](docs/user/running.md)

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

- **age3-compute-api** – API for compute modules,
- **age3-console** – implementation of the console,
- **age3-core** – implementation of the computational node,
- **age3-examples** – compute examples,
- **age3-stream-agents** – stream-processing-based multi-agent-system.

Other directories include:

- `.ide` – suggested configuration for your IDEs (IntelliJ IDEA and Eclipse),
- `docs` – documentation (available also at <https://docs.age.agh.edu.pl/age3/>),
- `docker` – files required to build Docker images,
- `gradle` – Gradle wrapper.

## Links

* [Repository on GitLab](https://gitlab.com/age-agh/age3)
* [Maven Repository](https://repo.age.agh.edu.pl/repository/maven-public/)
* [Documentation](https://docs.age.agh.edu.pl/age3/)
* [Javadocs](https://docs.age.agh.edu.pl/javadocs/age3/develop/)
