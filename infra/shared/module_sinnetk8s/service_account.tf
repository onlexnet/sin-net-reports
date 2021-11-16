# Why we need service account?
# - In Azure DevOps, to connect Environment to k8s namespace we have to point a service account
resource "kubernetes_service_account" "service_account" {
  metadata {
    name = "sinnet-service-account"
    namespace = local.namespace_name
  }
  secret {
    name = "${kubernetes_secret.service_account_secret.metadata.0.name}"
  }
}

resource "kubernetes_secret" "service_account_secret" {
  metadata {
    name = "sinnet-service-account-secret"
    namespace = local.namespace_name
  }
}