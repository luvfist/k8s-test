# Howto

## Vorbereitungen
* Kubernetes Dashboard installieren

~~~~
https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/
~~~~


## Namepsace erstellen

* JSON File erzeugen mit Inhalt

~~~~
{
  "apiVersion": "v1",
  "kind": "Namespace",
  "metadata": {
    "name": "k8s-test",
    "labels": {
      "name": "k8s-test"
    }
  }
}
~~~~

* Namespace in Kubernetes erzeugen

~~~~
kubectl create -f k8s-test-namespace.json
~~~~

* kontrollieren ob der Namespace angelegt wurde

~~~~
kubectl get namespaces
~~~~

* Istio sidecar injection aktivieren

~~~~
kubectl label namespace k8s-test istio-injection=enabled
~~~~

## Service und Deployment anlegen

~~~~
kubectl apply -f k8s-test.yaml
~~~~

~~~~
apiVersion: v1
kind: Service
metadata:
  name: k8s-test
  namespace: k8s-test
  labels:
    app: k8s-test
    service: k8s-test
spec:
  ports:
    - port: 8080
      name: http
  selector:
    app: k8s-test
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: k8s-test
  namespace: k8s-test
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-test-v1
  namespace: k8s-test
  labels:
    app: k8s-test
    version: v1
spec:
  replicas: 1
  selector:
    matchLabels:
      app: k8s-test
      version: v1
  template:
    metadata:
      labels:
        app: k8s-test
        version: v1
    spec:
      serviceAccountName: k8s-test
      containers:
        - name: k8s-test
          image: luvfist/k8s-test:latest
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
---
~~~~

## Ingress Gateway anlegen

~~~~
kubectl apply -f k8s-test-gateway.yaml
~~~~

~~~~
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: k8s-test-gateway
  namespace: k8s-test
spec:
  selector:
    istio: ingressgateway # use istio default controller
  servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
      hosts:
        - "luvfist.com"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: k8s-test
  namespace: k8s-test
spec:
  hosts:
    - "luvfist.com"
  gateways:
    - k8s-test-gateway
  http:
    - match:
        - uri:
            prefix: /hello
      route:
        - destination:
            host: k8s-test
            port:
              number: 8080
~~~~
