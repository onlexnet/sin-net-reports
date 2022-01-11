resource "azuredevops_project" "project" {
  name        = "onlex-sinnet"
  description = "New version of project (managed by Terraform)"
  features = {
    boards       = "disabled"
    repositories = "disabled"
    pipelines    = "enabled"
    testplans    = "disabled"
    artifacts    = "disabled"
  }
}