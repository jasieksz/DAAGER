---
title: Running and building AgE
---

# Running and building AgE

AgE 3 comprises of two separate applications:

- Node, located in the `age3-core` module.
  
  It is a non-interactive application that executes your computations that supports two modes of work:
  
  - waiting for commands from the console,
  - batch mode based on configurations passed using command line arguments. 
    
  It is possible to use multiple nodes in a single cluster to run a distributed computation.
  
- Console, located in the `age3-console` module.

  It is an interactive application that provides simple command-line access to the cluster of nodes.
  

Sometimes you may want to run one node along with the console on a single machine.
You can use so-called *standalone* mode of the console.

Both applications can be run from the Gradle configuration, from the standalone, executable jar or from the IDE.

## Using distribution JAR-s

To run the node, execute:

```bash
java -jar age3-core.jar
```

To run the, console:

```bash
java -jar age3-console.jar
```

> **Note:** console requires that at least one node is run and accessible.

To run the standalone node with the console:

```bash
java -jar age3-console.jar standalone
```

> **Note:** both the non-interactive node and the standalone modes are not really useful when you only have the `age3-console` package.
> You should build examples or your own modules and path them to the console (using directory described in the documentation)
> and to the node (using `jars` parameter of the `computation.load` command).

## Using distribution TAR file

Unpack the tarball, and then run the following command to start the console:

```bash
age3-console/bin/age3-console
```

Or, for starting the standalone node:

```bash
age3-console/bin/age3-console standalone
```

In a similar way, you can build and start the non-interactive node.

```bash
age3-core/bin/age3-core
```

## Using Docker

To run nodes, you can also use Docker:

```bash
docker pull registry.gitlab.com/age-agh/age3/age3-node:develop
docker run -v /local/dependencies/path:/dependencies --net=host age3-node:develop
```

You can replace *develop* with the tag of your choice.

All JAR files located in the `dependencies` volume will be added to the classpath.
You can also upload files to this volume when node is already running and then load them using the `jars` parameter of the `computation.load` command.


> **Note**: you can find the list of all Docker images on Gitlab: https://gitlab.com/age-agh/age3/container_registry

## Using Gradle

If you have downloaded the sources, you can use Gradle to build and run nodes and the console. 

To run the node execute:

```bash
./gradlew age3-core:node
```

To run the console execute:

```bash
./gradlew --no-daemon --console=plain age3-console:shell
```

> **Note:** console requires that at least one node is run and accessible.

To run the standalone node with the console:

```bash
./gradlew --no-daemon --console=plain age3-console:standalone
```

> **Note:** both the non-interactive node and the standalone modes are not really useful when you only have the `age3-console` package.
> You should build examples or your own modules and path them to the console (using directory described in the documentation)
> and to the node (using `jars` parameter of the `computation.load` command).

To pass arguments to Gradle tasks, you can use the `appArgs` property, for example:

```bash
./gradlew age3-core:node -PappArgs="['classpath:pl/edu/agh/age/compute/stream/example/spring-stream-example.xml']"
```

## Building distribution files

### Executable JAR

To build the executable JAR file for the console, execute:

```bash
./gradlew age3-console:shadowJar
```

You will find the built file in `age3-console/build/libs/`.

Similarly, to build the executable JAR file for the node, execute:

```bash
./gradlew age3-core:shadowJar
```

And you will find the built file in `age3-core/build/libs/`.

### Distribution TAR

To build the distribution TAR file for the console, execute:

```bash
./gradlew age3-console:shadowDistTar
```

You will find the built file in `age3-console/build/distributions/`.

Similarly, to build the distribution TAR file for the node, execute:

```bash
./gradlew age3-core:shadowDistTar
```

And you will find the built file in `age3-core/build/distributions/`.
