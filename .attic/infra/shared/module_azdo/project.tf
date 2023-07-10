data "azuredevops_project" "sinnet" {
  name = "sinnet"
}

resource "azuredevops_serviceendpoint_kubernetes" "serviceendpoint-kubernetes" {
  project_id            = data.azuredevops_project.sinnet.id
  service_endpoint_name = var.namespace_name
  apiserver_url         = local.apiserver_url
  authorization_type    = "ServiceAccount"

  service_account {
    token   = base64encode(local.service_account_token)
    ca_cert = base64encode(local.service_account_ca_cert)
  }
}
