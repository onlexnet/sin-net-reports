resource "azurerm_static_web_app" "webapp" {
  name                = var.subdomain
  resource_group_name = var.resource_group.name
  location            = var.resource_group.location
}

