output "static_app_api_key" {
  value = azurerm_static_web_app.webapp.api_key
}

output "webapp_fqdn" {
  value = azurerm_static_web_app.webapp.default_host_name
}

output name {
  value = azurerm_static_web_app.webapp.name
}

output static_web_app_id {
  value = azurerm_static_web_app.webapp.id
}
