Build and push local version of the app

```bash
eval $(minikube docker-env)
docker build . -t sinnet.azurecr.io/webapp:latest
```

Static Web APp configuration: https://learn.microsoft.com/en-us/azure/static-web-apps/configuration
