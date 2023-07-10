resource "azurerm_application_insights" "default" {
  name                = "appinsights-webapi"
  location            = var.resourcegroup.location
  resource_group_name = var.resourcegroup.name
  application_type    = "java"
}
