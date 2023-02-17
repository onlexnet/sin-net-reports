#TBD


# One time operations 1:
# cert-manager
# https://cert-manager.io/docs/installation/helm/
helm repo add jetstack https://charts.jetstack.io
helm repo update

```bash
. initshell.sh localhost
```

# One time operation 2:
# docker credentials for local kubernetes to allow pull images
# https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/#log-in-to-docker-hub
k create secret docker-registry regcred --docker-server=sinnet.azurecr.io --docker-username=sinnet --docker-password=<access key> --docker-email=siudeks@gmail.com

To configure expected dynamic properties on CI pipeline, you may use locally script as below:
```bash
export USERVICE_WEBAPI_IMAGE_TAG=latest
export USERVICE_PROJECTS_IMAGE_TAG=latest
export WEBAPP_IMAGE_TAG=latest
export USERVICE_TIMEENTRIES_IMAGE_TAG=latest
export REPORTS_IMAGE_TAG=latest
./makeconfig.sh
```
And later on apply on e.g. dev01
```bash
. initshell.sh localhost
# to install/upgrade  application:
h upgrade --install sinnet-reports . -f config.yaml
# or, to upgrade:
h upgrade sinnet-reports . -f config.yaml

```

##
## Target kubernetes configuration
- **microk8s with enabled ingress**. It is used in some places because ingress in microk8s has defined class and namespace
- **[cert-manager](https://cert-manager.io/)** installed with version 1.6
- **create k8s namespace *onlex-sinnet-localhost* and use it for further local deployment**
- **add position to hosts file: 127.0.0.1 sinnet.local** because such domain is expected by ingress in onlex-sinnet-localhost namespace
- create and self-signed cert (used locally instead of letsencrypt), and import the key to k8s to support secure connection
```bash
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/CN=sinnet.local/O=sinnet.local"
kubectl create secret tls portal-tls-secret --key tls.key --cert tls.crt -n onlex-sinnet-localhost
```
- next, start postgresql database and load secrets to allow application read secrets and connecto to database
```bash
k apply -f app-secrets.yaml
```

## Upgrade DAPR
Current version: 1.9.6

- on dev machine
```bash
# instal ldapr components in local docker to test without kubernetes
dapr uninstall
dapr init --runtime-version 1.9.6

# save updated dapr chart as dependency
cd charts
helm pull dapr/dapr --version=1.9.2

```


## Used artlcles
- https://www.aspecto.io/blog/distributed-tracing-with-opentelemetry-collector-on-kubernetes/
- https://docs.dapr.io/operations/hosting/kubernetes/kubernetes-deploy/#add-and-install-dapr-helm-chart
- https://cert-manager.io/docs/installation/helm/
- https://cert-manager.io/docs/configuration/acme/#creating-a-basic-acme-issuer
- https://github.com/jetstack/cert-manager/issues/1387
