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
  branch_pattern = "master"
}

resource "github_repository_environment_deployment_policy" "to-delete" {
  repository     = data.github_repository.sinnet.name
  environment    = github_repository_environment.main.environment
  branch_pattern = "siudeks/*"
}
