# AgE 3 – Dockerfile for node

## Building a container

To build a container, build the node shadow jar:

```
./gradlew age3-core:shadowJar
```
Copy the jar (`age3-core/build/libs/age3-core-VERSION-all.jar`) to this directory.

Run `docker build -t age3 .`

## Running the node from the container

You will usually run the node in the following way:
```
docker run -v /local/dependencies/path:/dependencies --net=host age3
```

The container needs the following volumes:
* `/dependencies` — with all jars needed for your application.
 
With the default Hazelcast configuration, multicast is required to work. Thus, you need to provide `--net=host` option
in order to bind to the host interface.


