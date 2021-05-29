# Deploy

## Pre requisits

1. Install **sudo snap install microk8s --classic**
1. Create local copy of microk8s configuration to integrate with local tools (like kubectl / VSCode)  
   **sudo microk8s kubectl config view --raw > $HOME/.kube/config** [more](https://github.com/ubuntu/microk8s)
1. **microk8s enable dns ingress**
1. Login to ACR to save credentials. It is required because ACR can't provide public repose and we have images hosteted in ACR
   so some integration is required.
   more - [here](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)
   **docker login sinnet.azurecr.io --username=sinnet --password=<Access key>**
   This will generate a config file *config.json*
   Now, use this this command to create secret based on just created config.json file
   **sudo microk8s.kubectl create secret generic regcred --type=kubernetes.io/dockerconfigjson --from-file .dockerconfigjson=$HOME/.docker/config.json**
   Perfect! now our kubectl is ready to pull image from private repo [using provided secret](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/#registry-secret-existing-credentials)
1. Now it time to add support for (let's encrypt)[https://cert-manager.io/docs/installation/kubernetes/]



## Apply cluster functionality

- **kubectl apply -f ingress.yaml** to define ingress routing
- **kubectl apply -f webapi-svc.yaml -f webapi-dep.yaml** to load backend service
- **kubectl apply -f lets-encrypt.yaml** to enable let's encrypt

## Q/A

## Hints
- DNS issue? https://kubernetes.io/docs/tasks/administer-cluster/dns-debugging-resolution/#known-issues
- [debugging DNS in Kubernetes](https://kubernetes.io/docs/tasks/administer-cluster/dns-debugging-resolution/)
- Run shell inside microk8s to work on inside bash: **kubectl run test --image=busybox -it --rm**

## Used articles
  * https://docs.microsoft.com/en-us/azure/azure-monitor/app/java-in-process-agent
  * https://sabbour.me/kubernetes-ci-cd-pipelines-using-azure-devops/
  * https://github.com/ubuntu/microk8s/issues/854
  * [microk8s dns show running status but remains in 0/1 state](https://github.com/ubuntu/microk8s/issues/845)
  * https://kubernetes.io/docs/tasks/administer-cluster/dns-debugging-resolution/
  * [No public DNS resolution inside pods](https://github.com/ubuntu/microk8s/issues/75)
  * https://cert-manager.io/docs/installation/kubernetes/
  * ? (https://runnable.com/blog/how-to-use-lets-encrypt-on-kubernetes)
  * https://github.com/hynese/k8sConfig
  * https://kndrck.co/posts/microk8s_ingress_example/
