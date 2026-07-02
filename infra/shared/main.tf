data "azurerm_client_config" "current" {}

module "resourcegroup" {
  source               = "./module_resourcegroup"
  application_name     = var.application_name
  environment_name     = var.environment_name
  environment_location = local.environment_location
  subscription_id      = data.azurerm_client_config.current.subscription_id
}

module "dns" {
  source              = "./module_dns"
  resource_group_name = module.resourcegroup.main.name
}

module "keyvault" {
  source            = "./module_keyvault"
  organization_name = local.organization_name
  application_name  = var.application_name
  environment_name  = var.environment_name
  resourcegroup     = module.resourcegroup.main
}

module "github" {
  source                                              = "./module_github"
  environment_name                                    = var.environment_name
  github_app_id                                       = var.github_app_id
  github_app_installation_id                          = var.github_app_installation_id
  github_app_pem                                      = var.github_app_pem
  webapp_time_prod_api_token                          = module.static_app_time.static_app_api_key
  ONLEXNET_INFRA_SECRET                               = module.keyvault.env.ONLEXNET_INFRA_SECRET
  ONLEXNET_TENANT_ID                                  = module.keyvault.env.ONLEXNET_TENANT_ID
  ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID               = module.keyvault.env.ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID
  ONLEXNET_INFRA_CLIENT_ID                            = module.keyvault.env.ONLEXNET_INFRA_CLIENT_ID
  ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES = module.container_apps_timeentries.containerapp_name
  ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI      = module.container_apps_webapi.containerapp_name
  ONLEXNET_SINNET_PRD01_FUNCTION_REPORT1_NAME         = module.fun_report1.function_app_name
  APPLICATIONINSIGHTS_CONNECTION_STRING               = module.application_insights.connection_string
  BACKEND_BASE_URL                                    = "https://${module.container_apps_webapi.webapi_fqdn}"
}

locals {
  domain_name = "onlex.net"
}

module "cloudflare" {
  source                         = "./module_cloudflare"
  webapi_prefix                  = "${var.application_name}-${var.environment_name}-api"
  webapi_fqdn                    = module.container_apps_webapi.webapi_fqdn
  webapp_prefix_time             = "time"
  webapp_fqdn_time               = module.static_app_time.webapp_fqdn
  report1_prefix                 = "report1"
  report1_fqdn                   = module.fun_report1.function_app_fqdn
  report1_domain_verification_id = module.fun_report1.custom_domain_verification_id
}

module "container_apps_env" {
  source                  = "./module_container_app_env"
  resource_group          = module.resourcegroup.main
  log_analytics_workspace = module.log_analytics_workspace.main
}

module "container_apps_timeentries" {
  source                  = "./module_container_app_timeentries"
  resource_group          = module.resourcegroup.main
  log_analytics_workspace = module.log_analytics_workspace.main
  env_id                  = module.container_apps_env.env_id
  env = {
    APPLICATIONINSIGHTS_CONNECTION_STRING = module.application_insights.connection_string
    GITHUB_USERNAME                       = module.keyvault.env.GITHUB_USERNAME
    CR_PAT                                = module.keyvault.env.CR_PAT
    DATABASE_HOST                         = module.database.database_host
    DATABASE_PORT                         = module.database.database_port
    DATABASE_NAME                         = module.database.database_name
    DATABASE_USERNAME                     = module.database.database_username
    DATABASE_PASSWORD                     = module.database.database_password
  }

}

module "container_apps_webapi" {
  source                  = "./module_container_app_webapi"
  resource_group          = module.resourcegroup.main
  log_analytics_workspace = module.log_analytics_workspace.main
  env_id                  = module.container_apps_env.env_id
  env = {
    APPLICATIONINSIGHTS_CONNECTION_STRING = module.application_insights.connection_string
    GITHUB_USERNAME                       = module.keyvault.env.GITHUB_USERNAME
    CR_PAT                                = module.keyvault.env.CR_PAT
    DATABASE_HOST                         = module.database.database_host
    DATABASE_PORT                         = module.database.database_port
    DATABASE_NAME                         = module.database.database_name
    DATABASE_USERNAME                     = module.database.database_username
    DATABASE_PASSWORD                     = module.database.database_password
    SINNETAPP_PROD_SECRET                 = module.keyvault.env.SINNETAPP_PROD_SECRET
  }

}

module "log_analytics_workspace" {
  source         = "./module_log_analytics_workspace"
  resource_group = module.resourcegroup.main
}


module "database" {
  source           = "./module_database"
  resource_group   = module.resourcegroup.main
  admin_password   = module.keyvault.env.SQL_ADMIN_PASSWORD
  environment_name = var.environment_name
  application_name = var.application_name
}

module "application_insights" {
  source           = "./module_application_insights"
  workspace_id     = module.log_analytics_workspace.main.id
  resource_group   = module.resourcegroup.main
  environment_name = var.environment_name
  application_name = var.application_name
}

module "fun_report1" {
  source           = "./module_fun_report1"
  resource_group   = module.resourcegroup.main
  environment_name = var.environment_name
  application_name = var.application_name
}

resource "azurerm_app_service_custom_hostname_binding" "module_fun_report1" {
  hostname            = "report1.onlex.net"
  app_service_name    = module.fun_report1.function_app_name
  resource_group_name = module.resourcegroup.main.name

  depends_on = [module.cloudflare]
}

module "static_app_time" {
  source         = "./module_static_app"
  name           = "webapp-time"
  resource_group = module.resourcegroup.main
}

module "static_app_time_domain" {
  source            = "./module_static_webapp_custom_domain"
  domain_name       = "time.onlex.net"
  static_web_app_id = module.static_app_time.static_web_app_id
  resource_group    = module.resourcegroup.main
  depends_on        = [module.cloudflare]
}
