# Setup
This project requires SBT to build, and a kubernetes runtime environment to deploy.  [Minikube](https://kubernetes.io/docs/setup/learning-environment/minikube/) and [minishift](https://www.okd.io/minishift/) should work.  This guide is implemented and tested using minikube.

Before applying the deploy scripts, you will need to set up all of the following components.  If you have minikube (or any other related components), you can skip this guide.  Note, you will have to edit configurations to point to the correct kubernetes service endpoints.  These configuration values can be found in the [menu-item-impl](menu-item-impl/deploy/kubernetes/menu-item-config.yaml) module, and the [restaurant-client](restaurant-client/deploy/kubernetes/restaurant-client-config.yaml) module.

## Installing minikube

I recommend using homebrew (or an equivalent package manager, if you are running on linux)

```shell script
brew install minikube
```

Next, we will need to make sure our minikube VM starts up with an appropriate allocation of memory.
```shell script
minikube start --cpus 4 --memory 10240
```

Once you have installed minikube, it is also important to install an ingress controller.  This will allow us to actually use an ingress.

```shell script
minikube addons enable ingress
```

And, finally, this configuration uses the URL `local-minikube.com` to point to our ingress.  You will want to edit your `/etc/hosts` file to map the result of `minikube ip` to `local-minikube.com`.  This command should add the appropriate line to `/etc/hosts` for you.
```shell script
printf "\n$(minikube ip) local-minikube.com" | sudo tee -a /etc/hosts
```
## Helm
[Helm](https://helm.sh/) is a utility that can be used to install applications in Kubernetes.  It can be thought of as a "package manager" for your Kubernetes environment.  We will use Helm to install Strimzi and Cassandra.

### Installation
The easiest option is to install via a package manger, as follows:

```shell script
brew install kubernetes-helm
```

If you need different instructions, refer to the [installation documentation on github](https://github.com/helm/helm#install)

Once you have installed Helm, you will need to [initialize it](https://helm.sh/docs/using_helm/#initialize-helm-and-install-tiller).
```shell script
helm init --history-max 200
```
## Strimzi
Strimzi is an [operator](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/), allowing us to create and manage Kafka instances with common kubectl commands.  To [install Strimzi via helm](https://strimzi.io/docs/latest/#deploying-cluster-operator-helm-chart-str):

1. First, add Strimzi charts to Helm, and update
    ```shell script
    helm repo add strimzi http://strimzi.io/charts/
    helm repo update
    ```

2. Create a kafka namespace for Strimzi/Kafka resources.  This helps you manage resources locally.
    ```shell script
    kubectl create namespace kafka
    ```
3. Install the strimzi operator.  This installation will be specifically for our restaurant, so we will specify options for isolation.
    ```shell script
    helm install --namespace kafka --name restaurant-kafka strimzi/strimzi-kafka-operator
    ```
    Note, you can install the operator in multiple names and namespaces, with different configurations.

4. Once you have installed the Strimzi operator, apply the included kafka yaml.
    ```shell script
    # Execute from the lagom-client-restaurant directory
    kubectl apply -f deploy/kubernetes/kafka/kafka-persistent-single.yaml -n kafka
    ```
    This will take time to propagate.  You can check the status of deployment if you like.
    ```shell script
    kubectl get pods -n kafka
    ```
    
## Cassandra
Using helm, we will install cassandra in the `cassandra` namespace in our minikube.

```shell script
helm repo add incubator https://kubernetes-charts-incubator.storage.googleapis.com/
helm repo update
helm install --namespace "cassandra" -n "cassandra" incubator/cassandra
```

This will take some time to initialize.