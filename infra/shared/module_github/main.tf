data "github_repository" "sinnet" {
  full_name = "onlexnet/sin-net-reports"
}

resource "github_repository_environment" "main" {
  environment = var.environment_name
  repository  = data.github_repository.sinnet.name
  
  # Reviewers can be configured here if needed
  # reviewers {
  #   users = [12345]  # GitHub user IDs
  # }

  deployment_branch_policy {
    # Whether only branches with branch protection rules can deploy to this environment.
    protected_branches = false

    custom_branch_policies = true
  }
}

resource "github_repository_environment_deployment_policy" "main" {
  repository  = data.github_repository.sinnet.name
  environment = github_repository_environment.main.environment
  # simple single name just to allow me deploy secondary version of WebApp
  branch_pattern = "webapp-test"
}

resource "github_actions_environment_secret" "webapp_prod_api_token" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "WEBAPP_PROD_API_TOKEN"
  plaintext_value = var.webapp_prod_api_token
}

resource "github_actions_environment_secret" "webapp_test_api_token" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "WEBAPP_TEST_API_TOKEN"
  plaintext_value = var.webapp_test_api_token
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

// secret APPLICATIONINSIGHTS_CONNECTION_STRING
resource "github_actions_environment_secret" "APPLICATIONINSIGHTS_CONNECTION_STRING" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "APPLICATIONINSIGHTS_CONNECTION_STRING"
  plaintext_value = var.APPLICATIONINSIGHTS_CONNECTION_STRING
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
