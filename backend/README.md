# Local development

## Kubernetes (minikube)

We are using [minikube](https://minikube.sigs.k8s.io/) to run Kubernetes locally.
This helps us test our services in Kubernetes environment before they are pushed into Cloud.

### (One time) setup

```shell script
brew install minikube
minikube config set memory 8192
minikube config set cpus 2
minikube start
minikube status
minikube addons enable metrics-server
```

### Start external requirements

Look into [infrastructure](../infrastructure/Readme.md)

## Deploy services

### (One time setup)

We use [tilt](https://github.com/tilt-dev/tilt) for development. Tilt automates all the steps from a code change to a new process: watching files, building container images, and bringing your environment up-to-date. Think docker build && kubectl apply or docker-compose up.

```sh
curl -fsSL https://raw.githubusercontent.com/tilt-dev/tilt/master/scripts/install.sh | bash
```

### Spin up

```
tilt up
tilt up auth-api
```

### Prometheus

https://github.com/coreos/kube-prometheus

```shell script
git clone https://github.com/coreos/kube-prometheus
cd kube-prometheus
# Create the namespace and CRDs, and then wait for them to be available before creating the remaining resources
kubectl create -f manifests/setup
until kubectl get servicemonitors --all-namespaces ; do date; sleep 1; echo ""; done
kubectl create -f manifests/
```
