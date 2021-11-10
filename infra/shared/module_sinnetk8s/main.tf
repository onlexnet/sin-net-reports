# Because we do not hae ability to handle Arc enabled kubernetes, we need to access kubernetes
# in traditional way assuming current context has access to kubernetes
# https://github.com/terraform-providers/terraform-provider-azurerm/issues/10635

provider "kubernetes" {
  
  host = var.k8s_host

  client_certificate     = "${file("~/onlex_infra.crt")}"
  client_key             = "${file("~/onlex_infra.key")}"
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
    name = "webapp-config"
    namespace = local.namespace_name
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
  }
}

resource "kubernetes_secret" "customers_db_user_name" {
  metadata {
    name = "customers-db-user-name"
    namespace = local.namespace_name
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
  }
}
