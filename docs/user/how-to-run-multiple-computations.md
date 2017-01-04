# How to run multiple computations?

A common scenario is to run repeated experiments with the same or similar configuration in each execution.
AgE tries to facilitate such a scenario by making it possible to run multiple computations one after another.

## Using NodeBootstrapper arguments

The quickest but the least flexible solution is to provide Spring XML configuration files as arguments to NodeBootstrapper.

### Running from the distTar

Generate and distribute the distTar, then unpack it and run:
```bash
age3-core-VERSION/bin/age3-core file:conf/1.xml file:conf/2.xml
```

### Running from the Gradle task

You can also run it from the source code using Gradle:
```bash
./gradlew node -PappArgs="['file:conf/1.xml', 'file:conf/2.xml']"
```

## Using console

When using the console (or standalone node with the console) you can start multiple computations using the following commands:

```js
computation.load({ config:'file:conf/1.xml' });
computation.start();
computation.waitUntilFinished();
computation.clean();

computation.load({ config:'file:conf/2.xml' });
computation.start();
computation.waitUntilFinished();
computation.clean();
```

Or you can prepare the whole Javascript file and run it in the batch mode of the console:

For example, save this file as `configuration.js`:
```js
for (var i = 1; i <= 2; i++) {
	computation.load({ config:'file:conf/' + i + '.xml' });
	computation.start();
	computation.waitUntilFinished();
	computation.clean();
}
```

And run:
```bash
age3-console-VERSION/bin/age3-console configuration.js
```

As always, it is also possible to run it from Gradle:
```bash
./gradlew age3-console:shell -PappArgs="['configuration.js']"
```

## Using "client" API

It is also possible to use the internal, not-really-documented API located in the `pl.edu.agh.age.client` package.
This is API used by the console to perform its tasks.

You need to have implementations injected to your class and it need to be created by Spring:

```java
public final class MyClass {

	private final WorkerServiceClient workerServiceClient;

	@Inject	public MyClass(final WorkerServiceClient workerServiceClient) {
		this.workerServiceClient = requireNonNull(workerServiceClient);
	}
}
```

