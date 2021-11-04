Used articles:
* https://www.techrunnr.com/create-users-and-define-rbac-in-kubernetes/ to create certs and admin role for onlex_infra user. Target k8s cluster has onlex_infra user as admin

Steps applied:
* on remote cluster (logged as user siudeks):
  * openssl genrsa -out onlex_infra.key 2048
  * openssl req -new -key onlex_infra.key -out onlex_infra.csr -subj "/CN=onlex_infra/O=admin"
  * openssl x509 -req -in onlex_infra.csr -CA /var/snap/microk8s/current/certs/ca.crt -CAkey /var/snap/microk8s/current/certs/ca.key -CAcreateserial -out onlex_infra.crt -days 500
  * microk8s enable rbac
  * create onlex_infra_role.yaml file to define role for onlex_infra
    ```yaml
    apiVersion: rbac.authorization.k8s.io/v1
    kind:  ClusterRole
    metadata:
      # "namespace" omitted since ClusterRoles are not namespaced
      name: infra
    rules:
    - apiGroups: ["*"]
      resources: ["*"]
      verbs: ["*"]

    ---

    apiVersion: rbac.authorization.k8s.io/v1
    kind: ClusterRoleBinding
    metadata:
      annotations:
      name: infra-rolebinding
    roleRef:
      apiGroup: rbac.authorization.k8s.io
      kind: ClusterRole
      name: infra
    subjects:
    - apiGroup: rbac.authorization.k8s.io
      kind: Group
      name: admin
    ```
  * apply new RBAC role
    ```bash
    kubectl apply onlex_infra_role.yaml
    ```
* on local server:
  * scp <USERNAME>@raport.sin.net.pl:/home/siudeks/onlex_infra.* .

