# Deploy

## Pre requisits

1. Install **sudo snap install microk8s --classic**
1. Produce local configuration to integrate wit local tools (like kubectl / VSCode)  
   **sudo microk8s kubectl config view --raw > $HOME/.kube/config** [more](https://github.com/ubuntu/microk8s)
1. microk8s enable dashboard ingress
1. Login to ACR to save credentials. It is required because ACR can't provide public repose and we have images hosteted in ACR
   so some integration is required.
   more - [here](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)
   docker login sinnet.azurecr.io --username=sinnet --password=<Access key>  
   This will generate a config file.
   *ls -la ~/.docker/config.json*
   Now, use this this command to create secret.
   *kubectl create secret generic regcred --type=kubernetes.io/dockerconfigjson --from-file .dockerconfigjson=$HOME/.docker/config.json*
   Perfect! now our kubectl is ready to pull image from private repo [using provided secret](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/#registry-secret-existing-credentials)



## Apply cluster functionality

- **kubectl apply -f ingress.yaml** to define ingress routing
- **kubectl apply -f backend-svc.yaml -f backend-dep.yaml** to load backend service

tls configuration given from https://docs.microsoft.com/en-us/azure/aks/ingress
We use Kubernetes Secrets to configure Spring application Refer to https://developers.redhat.com/blog/2017/10/04/configuring-spring-boot-kubernetes-secrets/#secrets-as-env-vars for an example

internal database ip: 172.16.1.4

database user password: posesor posesor jhasgdjhasTYFRYT-876ghfhg

So: on local env: kubectl create secret generic posesor-mongodb --from-literal=mongodb.posesor.prod=mongodb://posesor:jhasgdjhasTYFRYT-876ghfhg@13.80.128.133/posesor

how to add secret on kubecluster? kubectl create secret generic posesor-mongodb --from-literal=mongodb.posesor.prod=mongodb://posesor:jhasgdjhasTYFRYT-876ghfhg@172.16.1.4/posesor

How to update image from cli to e.g. 0.0.18? kubectl set image deployment/backend-dep webapi=respekto/posesor-host:0.0.18

Note: Because I use kubectl apply -f . to use wholde folder, azure.pipeline file can't have well-known name azure-pipelines.yaml because is recognized as invalid kubernetes file by kubectl, so a bit different name is used.


## Used articles

* https://kndrck.co/posts/microk8s_ingress_example/
