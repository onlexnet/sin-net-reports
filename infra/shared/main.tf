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
  webapp_prod_api_token                               = module.static_app_prod.static_app_api_key
  webapp_test_api_token                               = module.static_app_test.static_app_api_key
  ONLEXNET_INFRA_SECRET                               = module.keyvault.env.ONLEXNET_INFRA_SECRET
  ONLEXNET_TENANT_ID                                  = module.keyvault.env.ONLEXNET_TENANT_ID
  ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID               = module.keyvault.env.ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID
  ONLEXNET_INFRA_CLIENT_ID                            = module.keyvault.env.ONLEXNET_INFRA_CLIENT_ID
  ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES = module.container_apps_timeentries.containerapp_name
  ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI      = module.container_apps_webapi.containerapp_name
}

locals {
  webapp_subdomain_prod = var.environment_name != "prd01" ? "${var.application_name}-${var.environment_name}" : var.application_name
  webapp_subdomain_test = "${local.webapp_subdomain_prod}-test"
  domain_name           = "onlex.net"
}

module "static_app_prod" {
  source         = "./module_static_app"
  resource_group = module.resourcegroup.main

  custom_domain = "${local.webapp_subdomain_prod}.onlex.net"
}

module "static_app_test" {
  source         = "./module_static_webapp"
  resource_group = module.resourcegroup.main
  subdomain      = local.webapp_subdomain_test
}

module "cloudflare" {
  source             = "./module_cloudflare"
  webapp_prefix_prod = local.webapp_subdomain_prod
  webapp_prefix_test = local.webapp_subdomain_test
  webapp_fqdn_prod   = module.static_app_prod.webapp_fqdn
  webapp_fqdn_test   = module.static_app_test.webapp_fqdn
  webapi_prefix      = "${var.application_name}-${var.environment_name}-api"
  webapi_fqdn        = module.container_apps_webapi.webapi_fqdn
}

module "static_app_test_domain" {
  source            = "./module_static_webapp_custom_domain"
  domain_name       = "${local.webapp_subdomain_test}.${local.domain_name}"
  static_web_app_id = module.static_app_test.static_web_app_id
  resource_group    = module.resourcegroup.main
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
