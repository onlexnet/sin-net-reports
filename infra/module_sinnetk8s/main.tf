# copied from
# https://github.com/terraform-providers/terraform-provider-azurerm/issues/10635

provider "kubernetes" {
  
  host = var.k8s_host
  token = var.k8s_token
  insecure = true
}


resource "kubernetes_secret" "example" {
  metadata {
    name = "webapp-config"
    namespace = "onlex-sinnet-${var.environment_name}"
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
  }
}

resource "kubernetes_secret" "customers_db_user_name" {
  metadata {
    name = "customers_db_user_name"
    namespace = "onlex-sinnet-${var.environment_name}"
  }

  data = {
    appinsight_connection_string = var.secret_appinsight_connection_string
  }
}

