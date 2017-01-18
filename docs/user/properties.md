---
title: System properties in AgE
---

# System properties in AgE

This page presents all configuration properties accepted by AgE.
They can be passed using the standard `-D` option to the `java` command.

## Node

- `age.node.config=classpath_path_to.xml` – node Spring configuration to use. Default is **spring-node.xml**.
- `age.node.hazelcast.appender=(true|false)` – when **true** enables Logback appender 
  that passes all logging events to the global, distributed buffer accessible from the console. Default is **false**.
- `age.node.hazelcast.config.user=path_to.xml` – name (or path) of the additional configuration for Hazelcast.
  If provided, it overrides network configuration. Default is **classpath:hazelcast-network.xml**.

## Console

- `age.console.nodes=ip1:port1,ip2,ip3:port3` – comma-separated addresses of AgE nodes. Default is **127.0.0.1**.

