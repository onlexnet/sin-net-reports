output "static_app_api_key" {
  value = azurerm_static_web_app.webapp.api_key
}

output "webapp_fqdn" {
  value = azurerm_static_web_app.webapp.default_host_name
}
