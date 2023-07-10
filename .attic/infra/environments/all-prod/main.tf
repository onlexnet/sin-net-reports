resource "kubernetes_namespace" "default" {
  metadata {
    name = local.namespace_name
  }
}

module "kubernetes" {
  source         = "./module_kubernetes"
  namespace_name = local.namespace_name
}
