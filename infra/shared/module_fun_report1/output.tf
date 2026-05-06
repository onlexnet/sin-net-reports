output "function_app_name" {
  value = azurerm_linux_function_app.function.name
}

output "function_app_fqdn" {
  value = azurerm_linux_function_app.function.default_hostname
}

output "custom_domain_verification_id" {
  value = azurerm_linux_function_app.function.custom_domain_verification_id
}
