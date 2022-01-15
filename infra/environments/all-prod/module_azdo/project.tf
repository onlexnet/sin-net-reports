data "azuredevops_project" "sinnet" {
  name = "sinnet"
}

locals {
  apiserver_url = "https://raport.sin.net.pl:16443"
}

resource "azuredevops_serviceendpoint_kubernetes" "onlex-sinnet-stg01" {
  project_id            = data.azuredevops_project.sinnet.id
  service_endpoint_name = "onlex-sinnet-stg01"
  apiserver_url         = local.apiserver_url
  authorization_type    = "ServiceAccount"

  service_account {
    token   = base64encode(var.service_account_token)
    ca_cert = base64encode(var.service_account_ca_cert)
  }
}

resource "azuredevops_serviceendpoint_kubernetes" "onlex-sinnet-prd01" {
  project_id            = data.azuredevops_project.sinnet.id
  service_endpoint_name = "onlex-sinnet-prd01"
  apiserver_url         = local.apiserver_url
  authorization_type    = "ServiceAccount"

  service_account {
    token   = var.service_account_token
    ca_cert = var.service_account_ca_cert
  }
}

