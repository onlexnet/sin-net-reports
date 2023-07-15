output "webapi_fqdn" {
  # https://stackoverflow.com/questions/75735834/how-to-get-the-url-of-a-container-app-in-terraform
  value = azurerm_container_app.default.ingress[0].fqdn
}

