resource "kubernetes_namespace" "default" {
  metadata {
    name = local.namespace_name
  }
}
