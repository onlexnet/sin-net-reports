#TBD

To configure some dynamic properties on CI pipeline, you may use locally something as script below:
```bash
export WEBAPP_IMAGE_TAG=latest
export WEBAPI_IMAGE_TAG=latest
export REPORTS_IMAGE_TAG=latest
./makeconfig.sh
```
And later on apply on e.g. dev01
```bash
. initshell.sh dev01
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