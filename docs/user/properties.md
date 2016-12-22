# Configuration properties in AgE

This page presents all configuration properties accepted by AgE.
They can be passed using the standard `-D` option to the `java` command.

## Node

- `age.node.config=classpath_path_to.xml` – node Spring configuration to use. Default is **spring-node.xml**.
- `age.node.hazelcastAppender=(true|false)` – when **true** enables Logback appender
  that passes all logging events to the global, distributed buffer accessible from the console. Default is **false**.

