
resource "azurerm_static_web_app_custom_domain" "main" {
  static_web_app_id = var.static_web_app_id
  domain_name       = var.domain_name
  validation_type   = "dns-txt-token"
}

# resource "azurerm_static_site_custom_domain" "sinnet" {
#   static_site_id  = azurerm_static_web_app.webapp.id
#   domain_name     = var.domain_name
#   validation_type = "cname-delegation"
# }


