# Why we need service account?
# - In Azure DevOps, to connect Environment to k8s namespace we have to point a service account
# more: https://raaviblog.com/how-to-create-azure-service-connection-to-connect-to-kubernetes/
resource "kubernetes_service_account" "azure-devops-cluster-svc" {
  metadata {
    name = "azure-devops-cluster-svc"
    namespace = local.namespace_name
  }
}

# Minimal role required to connect namespace to Azure DevOps
resource "kubernetes_cluster_role" "azure-devops-cluster-svc" {
  metadata {
    name = "azure-devops-cluster-svc"
  }

  rule {
    api_groups     = [""]
    resources      = ["namespaces"]
    verbs          = ["get"]
  }

}

resource "kubernetes_cluster_role_binding" "azure-devops-cluster-svc-binding" {
  metadata {
    name      = "azure-devops-cluster-svc-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = kubernetes_cluster_role.azure-devops-cluster-svc.metadata[0].name
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.azure-devops-cluster-svc.metadata[0].name
    namespace = local.namespace_name
  }
}

