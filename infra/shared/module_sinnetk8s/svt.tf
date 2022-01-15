data "kubernetes_service_account" "devops-cluster-svc" {
  metadata {
    name      = "devops-cluster-svc"
    namespace = var.namespace_name_system
  }
}

data "kubernetes_secret" "devops-cluster-svc" {
  metadata {
    name      = data.kubernetes_service_account.devops-cluster-svc.default_secret_name
    namespace = var.namespace_name_system
  }
}