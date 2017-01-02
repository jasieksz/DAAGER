# How to use AgE in your project?

AgE is intended to be used in two ways:
1. With already implemented computational modules and algorithms.
2. As a building block for new computational modules.

In the former case, usage is dependent on the module. This page describes the latter case.

## What is needed?

1. JDK (1.8+)
2. Gradle or Maven.

## In short – using sample project

Clone <https://gitlab.com/age-agh/age3-sample-project.git> repository and use it as a base for your project.

## Creating project from scratch

1. Create an empty Gradle/Maven project in your IDE.
2. Add the following Maven repository to your configuration: https://repo.age.agh.edu.pl/repository/maven-public/. For example, in Gradle:

    ```groovy
    repositories {
        mavenCentral()
        maven {
	        url "https://repo.age.agh.edu.pl/repository/maven-public/"
        }
    }
    ```

3. Add a dependency on **age3-core**. For example, in Gradle:

    ```groovy
    dependencies {
	    compile 'pl.edu.agh.age:age3-core:0.3-SNAPSHOT'
    }
    ```

4. Create a class implementing **Runnable** interface in `src/main/java/my/project/` directory, for example:

    ```java
    public final class SampleRunnable implements Runnable {
	    private static final Logger logger = LoggerFactory.getLogger(SampleRunnable.class);

	    @Override public void run() {
		    logger.info("This is my own runnable!");
	    }
    }
    ```
    
    `Runnable` implementation is required, as AgE expects it for the entry point of the computation.
    
5. Create a Spring config that defines bean **runnable** and put in the `src/main/resources/` directory:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

	    <bean id="runnable"
	          class="my.project.SampleRunnable"/>

    </beans>
    ```

6. Compile the project.
7. Run your computation in AgE:

    ```bash
    java -cp YOUR_CLASSPATH -enableassertions pl.edu.agh.age.node.NodeBootstrapper file:src/main/resources/spring-sample.xml
    ```
    
    Two things are important in this command:   
    * **YOUR_CLASSPATH** should be replaces by collected list of jars and your classes,
    * The only argument for **NodeBootstrapper** is a path to your Spring configuration. If it is not provided, the node will wait for configuration from the network (for example, passed from a console node).
    
    Using `-enableassertions` is encouraged.
    
    Of course, constructing the classpath is rather boring task. It's easier to use a Gradle task similar to this one:
    
    ```groovy
    task node(type: JavaExec) {
	    main = 'pl.edu.agh.age.node.NodeBootstrapper'
	    classpath = sourceSets.main.runtimeClasspath
	    standardOutput = System.out
	    standardInput = System.in
	    jvmArgs '-enableassertions'
	    args 'file:src/main/resources/spring-sample.xml'
    }
    ```
    
## Advanced API

If you want to use more advanced computational API, check the **pl.edu.agh.age.compute.api** package.

## Advanced configuration

### Network configuration for Hazelcast

By default, AgE uses a default Hazelcast configuration – nodes are discovered using multicast
and all discovered nodes join a single cluster. It is appropriate for local networks but does not work in more complicated scenarios.
If you want to change this configuration, you need to provide your own `<network>` section for the Hazelcast XML config.
AgE loads the network configuration from the file specified by the `age.node.hazelcast.config.user` property.
The first file found is then imported into the main configuration.
 
You can either:
- provide a file with the same name somewhere in your classpath,
- change the property to your preferred value (`classpath:` prefix is supported).

**Note:** if you use the console, you should change its settings accordingly. 
