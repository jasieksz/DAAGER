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

