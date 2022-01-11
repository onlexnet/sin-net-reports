provider "kubernetes" {

  host = var.sinnet_k8s_host

  client_certificate = file("~/onlex_infra.crt")
  client_key         = file("~/onlex_infra.key")
  insecure           = true
}

provider "azuredevops" {
  personal_access_token = var.onlex_sinnet_azdo_personal_token
  org_service_url = var.onlex_sinnet_azdo_service_url
}