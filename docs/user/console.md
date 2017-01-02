# Console

AgE provides a simple console that is intended to make cluster and computation management easier.
It is located in the [**age3-console** module](https://gitlab.com/age-agh/age3/tree/develop/age3-console).

The current version of the console is backed by Nashorn JavaScript engine. It make the full integration with Java code possible right from the console.

## Running console

### From the build JAR
To start the console, you need to have `age3-console` jar and all its dependencies. Then execute:
```bash
java -cp YOUR_JARS -Dhazelcast.logging.type=slf4j -Dlogback.configurationFile=pl/edu/agh/age/console/logback.groovy pl.edu.agh.age.console.ConsoleBootstrapper
```
Replacing YOUR_JARS with the proper classpath.

### From the source

You can either tun:
```bash
./gradlew --no-daemon age3-console:shell
```
or build the distribution tar and run the console using the generated script:
```bash
./gradlew age3-console:distShadowTar
cd age3-console/build/distribution/
tar -xvf age3-console-0.3-SNAPSHOT.tar
age3-console-0.3-SNAPSHOT/bin/age3-console
```

## Using console

After starting the console, you will be greeted with the message and the prompt:
```
Welcome to the AgE console. Type help() to see usage information.
AgE> 
```

You can use any JavaScript syntax but, as for now, line-end escape does not work (that means that you need to fit proper JS code in a single line).
Some of the readline features are available: history, backsearch (`Ctrl`-`r`), etc.
You can access any Java code from the console, but there are some commands specifically provided to help you manage AgE.
Type `help()` to see a list of them. You can also use `help(group)` to display help for a specific group of commands

To execute one of the default commands, you need to call the method on a command group, for example, to call `destroy` from `cluster` group,
you use:
```js
cluster.destroy()
```
Some commands require parameters. You pass them as a JS dictionary:
```js
cluster.nodes({ id: 1, longOutput: true })
```
Optional parameters may be omitted.

## Batch mode

Since version 0.4 console support a batch mode. Any path passed as an argument to the console will be read and executed as if it was typed in the interactive mode.

As the files are interpreted as a whole JavaScript script you can use any syntax (e.g. conditional expressions, loops, etc) that you like.
On the other hand, you need to remember that batch execution may trigger some order-related errors
if handling of previous command has not yet finished before new command is executed.
This may improve in the future, but currently you can use `java.lang.Thread#sleep` as a workaround for such problems.

You can always use `load()` function provided by Nashorn to load additional scripts.

The "standalone" mode also supports batch processing. Just pass paths to your scripts after "standalone" keyword.

## Connecting to nodes

By default, the console connects to the local AgE node (using IP address **127.0.0.1**).
If you want to connect to remote nodes, you need to define them using `age.console.nodes` property.
