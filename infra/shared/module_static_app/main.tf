resource "azurerm_static_site" "webapp" {
  name                = "webapp"
  resource_group_name = var.resource_group.name
  location            = var.resource_group.location

}

resource "azurerm_static_site_custom_domain" "sinnet" {
  static_site_id  = azurerm_static_site.webapp.id
  domain_name     = var.custom_domain
  validation_type = "cname-delegation"
}
