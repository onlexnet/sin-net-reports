provider "kubernetes" {

  host = var.sinnet_k8s_host

  client_certificate = file("~/onlex_infra.crt")
  client_key         = file("~/onlex_infra.key")
  insecure           = true
}

provider "azuredevops" {
  personal_access_token = var.onlex_sinnet_azdo_personal_token
  org_service_url       = var.onlex_sinnet_azdo_service_url
}

provider "azurerm" {

#  Error: Error ensuring Resource Providers are registered.
#  Terraform automatically attempts to register the Resource Providers it supports to
#  ensure it's able to provision resources.
#  If you don't have permission to register Resource Providers you may wish to use the
#  "skip_provider_registration" flag in the Provider block to disable this functionality.
  skip_provider_registration = true
  features {
  }
  
}

provider "random" {
}

provider "azuread" {
}

