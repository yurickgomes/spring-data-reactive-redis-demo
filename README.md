# Spring reactive redis demo

## Introduction

This is a kotlin + spring reactive redis demo app

## Topics covered by this example

* Redis `Standalone` mode
* Read from replicas using a `Master/Replica` topology
* Redis `Cluster` mode
* Read from replicas even on `Cluster` mode
* Redis `Key/Value`
* Redis `Key/HashValue`
* Connect using `RedisStaticMasterReplicaConfiguration` on environments that don't allow topology discovery

## Podman compose files to get redis up and running

Inside the `infra` directory there are examples of podman compose files both for Master/Replica and Cluster modes 

