# Run gateway in k8s

```shell
# create kind cluster
$ kind create cluster --config kind.yml
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
$ echo "127.0.0.1    gateway.local" | sudo tee -a /etc/hosts

$ kind load docker-image postgres:15
$ kind load docker-image romanowalex/gateway:v1.0
$ kind load docker-image romanowalex/dictionary:v1.0

$ helm repo add romanow https://romanow.github.io/helm-charts/
$ helm install postgres romanow/postgres --values postgres/values.yaml
$ helm install gateway romanow/java-service --values=gateway/values.yaml
$ helm install store romanow/java-service --values=dictionary/values.yaml

EOT
```
