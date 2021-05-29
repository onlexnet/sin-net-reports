resource "azurerm_resource_group" "default" {
    name     = "${var.resource_group_name}-env-${var.environment}"
    location = var.location
}

