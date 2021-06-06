# copied from
# https://github.com/terraform-providers/terraform-provider-azurerm/issues/10635

provider "kubernetes" {
  
  host = var.k8s_host
  token = var.k8s_token
  insecure = true
}

# resource "kubernetes_secret" "example" {
#   metadata {
#     name = "basic-auth"
#     namespace = "onlex-sinnet-prd"
#   }

#   data = {
#     username = "admin"
#     password = "P4ssw0rd"
#   }

#   type = "kubernetes.io/basic-auth"
# }

