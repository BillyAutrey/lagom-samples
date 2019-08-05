# Menu Item

Simple Lagom service, deployable and runnable with Cassandra and Kafka in Kubernetes.

## Minikube notes
You will need at least 8 gigabytes of RAM allocated to your minikube environment in order to run this.  If your minikube is not set up with enough memory, services will fail to load completely.  Symptoms of this usually manifest as containers that die suddenly, and "Out of Memory" warnings and errors in Minikube's status.

## Installing Kafka and Cassandra via helm


## Deployment
You will first need to build this project, and publish it to a repository.  If you are running in minikube, you can do the following:

```shell script
eval $(minikube docker-env)
```
This will set the local terminal's environment up so that docker commands (including compose) will execute using minikube's Docker environment.

In the same terminal, execute the following:

```sbt
sbt menu-item-impl/docker:publishLocal
```

Once this is up, you should be able to execute the following:

```shell script
kubectl apply -f deploy/kubernetes
```

This will apply all of the yaml files in the deploy/kubernetes folder (but not the debug subfolder.  This command does not recurse)