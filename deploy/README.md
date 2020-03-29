tls configuration given from https://docs.microsoft.com/en-us/azure/aks/ingress
We use Kubernetes Secrets to configure Spring application Refer to https://developers.redhat.com/blog/2017/10/04/configuring-spring-boot-kubernetes-secrets/#secrets-as-env-vars for an example

internal database ip: 172.16.1.4

database user password: posesor posesor jhasgdjhasTYFRYT-876ghfhg

So: on local env: kubectl create secret generic posesor-mongodb --from-literal=mongodb.posesor.prod=mongodb://posesor:jhasgdjhasTYFRYT-876ghfhg@13.80.128.133/posesor

how to add secret on kubecluster? kubectl create secret generic posesor-mongodb --from-literal=mongodb.posesor.prod=mongodb://posesor:jhasgdjhasTYFRYT-876ghfhg@172.16.1.4/posesor

How to update image from cli to e.g. 0.0.18? kubectl set image deployment/backend-dep webapi=respekto/posesor-host:0.0.18