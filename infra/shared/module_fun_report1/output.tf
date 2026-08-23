output "function_app_name" {
  value = azurerm_function_app_flex_consumption.function.name
}

output "function_app_fqdn" {
  value = azurerm_function_app_flex_consumption.function.default_hostname
}

output "custom_domain_verification_id" {
  value = azurerm_function_app_flex_consumption.function.custom_domain_verification_id
}

output "shared_secret" {
  description = "Secret shared with webapi to authenticate calls to fun_report1 (X-Report1-Secret header)."
  value       = random_password.shared_secret.result
  sensitive   = true
}
