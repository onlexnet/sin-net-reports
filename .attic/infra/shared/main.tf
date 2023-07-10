module "application" {
  source           = "./module_application"
  application_name = var.application_name
  environment_name = var.environment_name
}

module "resourcegroup" {
  source               = "./module_resourcegroup"
  application_name     = var.application_name
  environment_name     = var.environment_name
  environment_location = var.environment_location
  subscription_id      = var.env_subscription_id
}

module "appinsights" {
  source        = "./module_appinsights"
  resourcegroup = module.resourcegroup.resourcegroup
}

data "azurerm_container_registry" "sinnet" {
  provider            = azurerm.shared
  name                = "sinnet"
  resource_group_name = "sinnet-default-manual"
}

module "sinnetk8s" {
  source                              = "./module_sinnetk8s"
  k8s_host                            = var.sinnet_k8s_host
  k8s_token                           = var.sinnet_k8s_token
  secret_appinsight_connection_string = module.appinsights.connection_string
  config_services_database_name       = module.database.services_database_name
  config_services_database_username   = module.database.services_database_username
  config_services_database_password   = module.database.services_database_password
  config_reports_storage_address      = module.storage_account.reports_storage_address
  config_reports_container_name       = module.storage_account.reports_container_name
  environment_name                    = var.environment_name
  docker_registry_username            = data.azurerm_container_registry.sinnet.admin_username
  docker_registry_password            = data.azurerm_container_registry.sinnet.admin_password
  docker_registry_server              = data.azurerm_container_registry.sinnet.login_server
  namespace_name_system               = "onlex-sinnet-prod"
}

module "keyvault" {
  source           = "./module_keyvault"
  application_name = var.application_name
  environment_name = var.environment_name
  resourcegroup    = module.resourcegroup.resourcegroup

  appinsight_connection_string = module.appinsights.connection_string
  support_security_group_name  = var.support_security_group
}

module "database" {
  source           = "./module_database"
  application_name = var.application_name
  environment_name = var.environment_name
  db_user_name     = var.psql_infrauser_name
  db_user_password = var.psql_infrauser_password
}

module "storage_account" {
  source           = "./module_storage_account"
  environment_name = var.environment_name
  resource_group   = module.resourcegroup.resourcegroup
  application_name = var.application_name
}

module "azdo" {
  source                  = "./module_azdo"
  environment_name        = var.environment_name
  namespace_name_system   = "onlex-sinnet-prod"
  namespace_name          = module.sinnetk8s.namespace_name
  devops-cluster-svc-data = module.sinnetk8s.devops-cluster-svc-data
}
