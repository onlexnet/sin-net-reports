resource "azurerm_resource_group" "default" {
  name     = "${var.application_name}-env-${var.environment_name}"
  location = var.environment_location
}
