# Why we need service account?
# - In Azure DevOps, to connect Environment to k8s namespace we have to point a service account
# more: https://raaviblog.com/how-to-create-azure-service-connection-to-connect-to-kubernetes/
resource "kubernetes_service_account" "devops-cluster-svc" {
  metadata {
    name      = "devops-cluster-svc"
    namespace = var.namespace_name
  }
}

# Minimal role required to connect namespace to Azure DevOps
resource "kubernetes_cluster_role" "devops-cluster-svc" {
  metadata {
    name = "devops-cluster-svc"
  }

  # required to connect namespace to ADO
  rule {
    api_groups = [""]
    resources  = ["namespaces"]
    verbs      = ["get"]
  }

  # required to deploy app to ADO

  rule {
    api_groups = ["*", "apps", "extensions"]
    resources  = ["*"]
    verbs      = ["get", "list", "watch", "create", "update", "patch", "delete"]
  }

}

resource "kubernetes_cluster_role_binding" "devops-cluster-svc-binding" {
  metadata {
    name = "devops-cluster-svc-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = kubernetes_cluster_role.devops-cluster-svc.metadata[0].name
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.devops-cluster-svc.metadata[0].name
    namespace = var.namespace_name
  }
}

data "kubernetes_secret" "devops-cluster-svc" {
  metadata {
    name      = kubernetes_service_account.devops-cluster-svc.default_secret_name
    namespace = var.namespace_name
  }
}
