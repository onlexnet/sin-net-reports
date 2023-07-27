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

resource "github_repository_environment_deployment_policy" "master" {
  repository     = data.github_repository.sinnet.name
  environment    = github_repository_environment.main.environment
  branch_pattern = "siudeks/*"
}

resource "github_actions_environment_secret" "azure_static_web_apps_api_token" {
  environment     = github_repository_environment.main.environment
  repository      = data.github_repository.sinnet.name
  secret_name     = "AZURE_STATIC_WEB_APPS_API_TOKEN"
  plaintext_value = var.azure_static_web_apps_api_token
}
