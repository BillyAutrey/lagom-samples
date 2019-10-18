# Lagom Samples [![Build Status](https://travis-ci.com/BillyAutrey/lagom-samples.svg?branch=master)](https://travis-ci.com/BillyAutrey/lagom-samples)

This includes projects to illustrate key concepts for Lagom projects, as of Lagom `1.5.4`.  Key implementation details include:

* API/Implementation separation examples.
* Persistent Entity implementations.
* Message Broker API through Kafka.
* Functional tests of all components.

## Restaurant

Contains a menu-item service, and an Akka HTTP client.  Key features:

* All components deploy successfully on Minikube.
* All components have working tests.
* Uses Cassandra to store Persistent Entity write-side data.
* Uses Kafka as a message broker, for Lagom Topics.
* Client represents one way that you can invoke Lagom APIs through the Lagom Client API.

## Sensor Stats

Contains a Lagom sensor service and an Akka HTTP client.  Key features:

* Sensor service allows publishing of statistics to defined sensor IDs.
* A Lagom topic (using the Kafka Message Broker API) exposes `SensorUpdated` events.
