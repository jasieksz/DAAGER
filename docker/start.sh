#!/usr/bin/env bash


java -cp age3-all.jar:'/dependencies/*.jar' -enableassertions -Dlogback.configurationFile=pl/edu/agh/age/node/logback.groovy pl.edu.agh.age.node.NodeBootstrapper
