#TBD

To configure expected dynamic properties on CI pipeline, you may use locally script as below:
```bash
export USERVICE_WEBAPI_IMAGE_TAG=latest
export WEBAPP_IMAGE_TAG=latest
export USERVICE_TIMEENTRIES_IMAGE_TAG=latest
export REPORTS_IMAGE_TAG=latest
./makeconfig.sh
```
And later on apply on e.g. dev01
```bash
. initshell.sh dev01

# One time operation 1:
# cert-manager
# https://cert-manager.io/docs/installation/helm/
h repo add jetstack https://charts.jetstack.io
helm repo update

# One time operation 2:
# docker credentials for local kubernetes to allow pull images
# https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/#log-in-to-docker-hub
k create secret docker-registry regcred --docker-server=sinnet.azurecr.io --docker-username=sinnet --docker-password=<access key> --docker-email=siudeks@gmail.com

# to install application:
h install sinnet-reports . -f config.yaml
# or, to upgrade:
h upgrade sinnet-reports . -f config.yaml

```

##
## Target kubernetes configuration
- **microk8s with enabled ingress**. It is used in some places because ingress in microk8s has defined class and namespace
- **[cert-manager](https://cert-manager.io/)** installed with version 1.6

## Used artlcles
- https://cert-manager.io/docs/installation/helm/
- https://cert-manager.io/docs/configuration/acme/#creating-a-basic-acme-issuer
- https://github.com/jetstack/cert-manager/issues/1387
