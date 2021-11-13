#TBD

To configure some dynamic properties on CI pipeline, you may use something as script below:
```bash
export WEBAPP_IMAGE_TAG=master-20211019.3
./makeconfig.sh
```
And later on apply on e.g. dev01
```bash
. initshell.sh dev01

```

##
## Target kubernetes configuration
- **microk8s with enabled ingress**. It is used in some places because ingress in microk8s has defined class and namespace
- **[cert-manager](https://cert-manager.io/)** installed with version 1.6

## Used artlcles
- https://cert-manager.io/docs/installation/helm/
- https://cert-manager.io/docs/configuration/acme/#creating-a-basic-acme-issuer
- https://github.com/jetstack/cert-manager/issues/1387