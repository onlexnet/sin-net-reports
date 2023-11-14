# data "azurerm_container_registry" "alldev" {
#   name                = "fin2setalldev"
#   resource_group_name = "fin2set-env-alldev"
# }


# Create user assigned identity and associated IAM role assignment
resource "azurerm_user_assigned_identity" "containerapp" {
  location            = var.resource_group.location
  name                = "containerappmi"
  resource_group_name = var.resource_group.name
}

resource "azurerm_container_app_environment" "default" {
  name                       = "prd01-env"
  location                   = var.resource_group.location
  resource_group_name        = var.resource_group.name
  log_analytics_workspace_id = var.log_analytics_workspace.id
}

