# Deploy

## Pre requisits

1. Remove previous version (if any) **sudo snap remove microk8s**
1. Install **sudo snap install microk8s --classic**
1. Create local copy of microk8s configuration to integrate with local tools (like kubectl / VSCode)  
   **sudo microk8s kubectl config view --raw > $HOME/.kube/config** [more](https://github.com/ubuntu/microk8s)
1. **microk8s enable dns ingress**
1. add raport.sin.net.pl as additional DNS as described **[here](https://github.com/ubuntu/microk8s/issues/849#issuecomment-562892386)**
1. Apply current state using teraform definition located in *infra* folder



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
