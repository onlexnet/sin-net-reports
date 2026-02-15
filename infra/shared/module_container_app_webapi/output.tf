output "webapi_fqdn" {
  # https://stackoverflow.com/questions/75735834/how-to-get-the-url-of-a-container-app-in-terraform
  value = azurerm_container_app.default.ingress[0].fqdn
}

# name of containerapp
output "containerapp_name" {
  value = azurerm_container_app.default.name
}

output "webapi_url" {
  description = "The complete URL of the WebAPI service (https://webapi_fqdn)"
  value       = "https://${azurerm_container_app.default.ingress[0].fqdn}"
}
