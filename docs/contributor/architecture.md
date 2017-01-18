---
title: Architecture
---

# Architecture

## Core module

The core module (located in **age3-core** Gradle module) should contain all of the services needed to deploy a distributed cluster of nodes, general compute API and general utilities.
It **should not** contain:

- compute-dependent classes,
- console-related classes,
- examples.

Generally — the less, the better.

Note: It is worth considering to extract compute API and utilities to separate module,
so the compute modules won't be forced to depend on the core.
However, our codebase is small as for now, so we still keep everything in the core.

## Core services

There are several core services, that are required for AgE 3 to work:

* discovery — performs (compute) node discovery,
* identity — provides info about compute nodes,
* lifecycle — manages the lifecycle of a compute node,
* topology — manages compute nodes topology (it's different from the optional compute-level topology),
* worker — manages task execution.

**NodeBootstrapper** is an entry point to the compute node,
it loads the Spring configuration and then waits till the lifecycle service is terminated.
All services are Spring components and they are started automatically or on demand (by other services).

Services communicate with services in the same node using Guava's EventBus.
The same services on different nodes communicate using their own channels
(usually Hazelcast's data structures as for now).

You can find more information about these services in their respective Javadocs.

## Compute modules design

Compute modules should be unaware of the distributed nature of the platform.
They should be able to define their unit of work, communicate these units, handle various events
(e.g. pause, data loss) but using the platform-provided utilities and not general tools.
That means that we do not want for compute to, for example, manage its own threads
or use separate communication mechanism beside these provided by the compute API (**pl.edu.agh.age.compute.api**).
