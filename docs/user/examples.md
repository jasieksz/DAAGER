---
title: Examples
---

# Examples

Examples are located in the `age3-examples` module.

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