data "github_user" "current" {
  username = ""
}

data "github_repository" "sinnet" {
  full_name = "onlexnet/sin-net-reports"
}

resource "github_repository_environment" "main" {
  environment = var.environment_name
  repository  = data.github_repository.sinnet.name
  reviewers {
    # users = [data.github_user.current.id]
  }

  deployment_branch_policy {
    protected_branches     = false
    custom_branch_policies = true
  }
}

resource "github_repository_environment_deployment_policy" "main" {
  repository     = data.github_repository.sinnet.name
  environment    = github_repository_environment.main.environment
  branch_pattern = "main"
}

resource "github_actions_environment_secret" "azure_static_web_apps_api_token" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "AZURE_STATIC_WEB_APPS_API_TOKEN"
  plaintext_value = var.azure_static_web_apps_api_token
}

resource "github_actions_environment_secret" "ONLEXNET_TENANT_ID" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "ONLEXNET_TENANT_ID"
  plaintext_value = var.ONLEXNET_TENANT_ID
}

resource "github_actions_environment_secret" "ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID"
  plaintext_value = var.ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID
}

resource "github_actions_environment_secret" "ONLEXNET_INFRA_CLIENT_ID" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "ONLEXNET_INFRA_CLIENT_ID"
  plaintext_value = var.ONLEXNET_INFRA_CLIENT_ID
}

resource "github_actions_environment_secret" "ONLEXNET_INFRA_SECRET" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "ONLEXNET_INFRA_SECRET"
  plaintext_value = var.ONLEXNET_INFRA_SECRET
}

resource "github_actions_environment_variable" "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES" {
  environment   = github_repository_environment.main.environment
  repository    = data.github_repository.sinnet.name
  variable_name = "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES"
  value         = var.ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES
}

resource "github_actions_environment_variable" "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI" {
  environment   = github_repository_environment.main.environment
  repository    = data.github_repository.sinnet.name
  variable_name = "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI"
  value         = var.ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI
}
