---
title: How to write your own compute module?
---

# How to write your own compute module?

The design of the platform eases the iterative development of compute modules.
You can start with a simple algorithm (that you could run standalone, without AgE)
and extend it to make use of the utilities that AgE provides.
 
The entry point to the computation is any class implementing the **Runnable** interface:
```java
package my.pkg;

public final class MyRunnable implements Runnable {
	@Override public void run() {
		// Here should be your code
	}
}
```

And that's enough, you can now start the AgE in standalone mode (compute node and console on a single physical node)
and load the class using the following command:
```
computation.load({ class: 'my.pkg.MyRunnable' })
```

Then start it:
```
computation.start()
```

And clean after it is finished:
```
computation.clean()
```

## Spring configuration

Although loading code to execute by class name is quick and easy,
in more complicated problems you may want to use Spring XML-based configuration.
To do this you just need to provide the configuration that has a bean with name **runnable** defined:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="runnable" class="my.pkg.MyRunnable"/>

</beans>
```

If you have a Spring configuration, you can omit executing code using the console.
The compute node can load and execute your code from a Spring configuration –
just pass the path (using `classpath:` or `file:` url) as an argument to the **NodeBootstrapper**. 

## Compute API and utilities

Now you can easily extend your code and make use of utilities provided in the **pl.edu.agh.age.compute.api** package.
To use them, you just need to add them as parameters to the constructor
and mark the constructor with the **@Inject** annotation, like this:
```java
public final class MyRunnable {
	@Inject public MyRunnable(final UnicastMessenger messenger) {
		// Your code
	}
}
```
You do not need to define these dependencies in XML file,
they are automatically injected when the compute configuration is loaded.

Note: Of course you can use any other method if injection supported by Spring (e.g. field injection). 

### Compute-level topology

In some computations it may be beneficial to introduce additional network topology between computational units.
It is especially useful in cases when there are multiple computation units on single node.

To aid this scenario AgE provides a topology service accessible with **TopologyProvider** API.

A quick example of it:
```java
Set<String> nodes = ImmutableSet.of("first", "second", "third"); 
topologyProvider.setTopology(Topology.unidirectionalRing());
topologyProvider.addNodes(nodes);
Map<String, Set<String>> neighbours = topologyProvider.neighboursOf("second");
```

This API requires two things:

- topology function – passed using `setTopology`,
- identifiers (any serializable type) of your computational units – passed using `addNodes`.  

In most cases functions located in `pl.edu.agh.age.compute.api.topology`
and `pl.edu.agh.age.compute.api.topology.Topology` can be used.
However you are free to provide your own implementation of the topology generator (implementing `Topology` interface).
If you need to change the topology after initialization, just use the `setTopology` method again.


Topology offered to compute levels use two concepts to locate nodes:

- identifiers defined by user,
- `String`-based annotations defined by the topology function.

Annotations make it possible to define more complicated relations beside simple "neighbourhood". 
For example, for bi-directional ring, two annotations are used: *left* and *right*,
so you can easily differentiate the direction of your messages.
