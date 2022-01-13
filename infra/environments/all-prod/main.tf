resource "kubernetes_namespace" "default" {
  metadata {
    name = local.namespace_name
  }
}

module "kubernetes" {
  source         = "./module_kubernetes"
  namespace_name = local.namespace_name
}

module "azdo" {
  source                  = "./module_azdo"
  service_account_token   = module.kubernetes.devops-cluster-svc-token
  service_account_ca_cert = module.kubernetes.ca-cert
}