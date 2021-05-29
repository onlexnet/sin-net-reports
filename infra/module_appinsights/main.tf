resource "azurerm_application_insights" "default" {
  name                = "appinsights"
  location            = var.resourcegroup.location
  resource_group_name = var.resourcegroup.name
  application_type    = "web"
}

output "instrumentation_key" {
  value = azurerm_application_insights.default.instrumentation_key
}

output "app_id" {
  value = azurerm_application_insights.default.app_id
}