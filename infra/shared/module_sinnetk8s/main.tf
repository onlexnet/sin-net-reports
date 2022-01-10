# Because we do not hae ability to handle Arc enabled kubernetes, we need to access kubernetes
# in traditional way assuming current context has access to kubernetes
# https://github.com/terraform-providers/terraform-provider-azurerm/issues/10635

provider "kubernetes" {

  host = var.k8s_host

  client_certificate = file("~/onlex_infra.crt")
  client_key         = file("~/onlex_infra.key")
  # cluster_ca_certificate = "${file("~/.kube/cluster-ca-cert.pem")}"}
  insecure = true
}

locals {
  namespace_name = "onlex-sinnet-${var.environment_name}"
}

resource "kubernetes_namespace" "default" {
  metadata {
    name = local.namespace_name
  }
}

resource "kubernetes_secret" "example" {
  metadata {
    name      = "webapp-config"
    namespace = local.namespace_name
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
  }
}

resource "kubernetes_secret" "default" {
  metadata {
    name      = "app-secrets"
    namespace = local.namespace_name
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
    services_database_name       = var.config_services_database_name
    services_database_username   = var.config_services_database_username
    services_database_password   = var.config_services_database_password
  }
}

resource "kubernetes_config_map" "default" {
  metadata {
    name      = "app-configuration"
    namespace = local.namespace_name
  }
  data = {
    reports_storage_address = var.config_reports_storage_address
    reports_container_name  = var.config_reports_container_name
  }

  depends_on = [
    kubernetes_namespace.default
  ]
}
