output "function_app_name" {
  value = azurerm_function_app_flex_consumption.function.name
}

output "function_app_fqdn" {
  value = azurerm_function_app_flex_consumption.function.default_hostname
}

output "custom_domain_verification_id" {
  value = azurerm_function_app_flex_consumption.function.custom_domain_verification_id
}
