resource "azuredevops_environment" "devops-environment" {
  project_id = data.azuredevops_project.sinnet.id
  name       = upper(var.environment_name)
}
