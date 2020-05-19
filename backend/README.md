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

### Deploy services

Requires `tilt`

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
