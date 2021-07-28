module "application" {
  source = "./module_application"
  application_name = var.application_name
  environment_name = var.environment_name
}

module "resourcegroup" {
  source = "./module_resourcegroup"
  application_name     = var.application_name
  environment_name     = var.environment_name
  environment_location = var.environment_location
  subscription_id      = var.subscription_id
}

module "appinsights" {
  source = "./module_appinsights"
  resourcegroup = module.resourcegroup.resourcegroup
}

module "sinnetk8s" {
  source = "./module_sinnetk8s"
  k8s_host                            = var.sinnet_k8s_host
  k8s_token                           = var.sinnet_k8s_token
  secret_appinsight_connection_string = module.appinsights.connection_string
  environment_name                    = var.environment_name
}

module "keyvault" {
  source = "./module_keyvault"
  application_name = var.application_name
  environment_name = var.environment_name
  resourcegroup = module.resourcegroup.resourcegroup

  appinsight_connection_string = module.appinsights.connection_string
}

module "database" {
  source = "./module_database"
  application_name = var.application_name
  environment_name = var.environment_name
  db_user_name = var.psql_infrauser_name
  db_user_password = var.psql_infrauser_password
}

# data "azurerm_client_config" "current" {
# #   client_id     = data.azurerm_client_config.current.client_id
# #   client_secret = data.azurerm_client_config.current.
# #   tenant_id     = data.azurerm_client_config.current.tenant_id
# }



# data "azurerm_container_registry" "example" {
#     provider = azurerm.sinnet-production
#     name                = "sinnet"
#     resource_group_name = "sinnet-default-manual"
# }

# resource "azurerm_storage_account" "default" {
#     resource_group_name = azurerm_resource_group.default.name
#     name = "${var.resource_group_name}${var.environment}storage"
#     location = var.location
#     account_tier = "Standard"
#     account_replication_type = "LRS"
# }

