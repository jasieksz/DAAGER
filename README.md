# AgE 3

AgE 3 is a new version and complete rewrite of the distributed agent-based computational platform
[AgE](https://www.age.agh.edu.pl/).

**This is beta software** and is still under heavy development. Although this version is released, it is only
a *snapshot* of a current development branch and there is no guarantee that future version will provide the same
functionality or API.

## Quickstart

AgE can be run in three modes:

* computational, non-interactive node (part of the cluster) — `age3-core` module,
* console-only for accessing the cluster — `age3-console` module,
* standalone single node with console — `age3-console` module.

To run non-interactive node, firstly build the :

To run the console or standalone node, firstly build the fatjar and startup scripts:
```
./gradlew age3-console:distShadowTar
```
This will create a tar file in the `age3-console/build/distributions/` directory.
`cd` into it, unpack the tarball, and then run the following command to start the console:
```
age3-console-0.3-SNAPSHOT/bin/age3-console
```
Or, for starting the standalone node:
```
age3-console-0.3-SNAPSHOT/bin/age3-console standalone
```

To run the computational node, you can build a fatjar similarly, or start it directly from the Gradle wrapper:
```
./gradlew age3-core:node
```

## Examples

Simple examples are provided in the `age3-examples` module. You can build it and start the same way, as a console and
standalone node:
```
./gradlew age3-examples:distShadowTar
cd age3-examples/build/distribution/
tar -xvf age3-examples-0.3-SNAPSHOT.tar
age3-examples-0.3-SNAPSHOT/bin/age3-examples standalone
```

Then check the `test list-examples` command.

## Links

* [Repository on GitLab](https://gitlab.com/age-agh/age3)
* [Javadocs](https://www.age.agh.edu.pl/docs/dev/javadocs/)