# You must add a required_providers block to every module that will create resources with this provider. If you do not explicitly require integrations/github in a submodule, your terraform run may break in hard-to-troubleshoot ways.
terraform {
  required_providers {
    github = {
      source  = "integrations/github"
      version = "~> 6.0"
    }
  }
}

# Configure the GitHub Provider with App authentication
# This approach doesn't expire like Personal Access Tokens
provider "github" {
  owner = "onlexnet"
  
  app_auth {
    id              = var.github_app_id
    installation_id = var.github_app_installation_id
    pem_file        = var.github_app_pem
  }
}
