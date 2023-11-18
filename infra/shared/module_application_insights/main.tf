resource "azurerm_application_insights" "main" {
  name                = "${var.application_name}-${var.environment_name}"
  location            = var.resource_group.location
  resource_group_name = var.resource_group.name
  workspace_id        = var.workspace_id
  application_type    = "java"
}
