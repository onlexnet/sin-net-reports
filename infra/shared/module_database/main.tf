resource "azurerm_mssql_server" "default" {
  name                         = "fin2set-${var.environment_name}"
  resource_group_name          = var.resource_group.name
  location                     = var.resource_group.location
  version                      = "12.0"
  administrator_login          = "sinnet"
  administrator_login_password = var.admin_password
  minimum_tls_version          = "1.2"

  # azuread_administrator {
  #   login_username = "AzureAD Admin"
  #   object_id      = "00000000-0000-0000-0000-000000000000"
  # }

  tags = {
    environment = var.environment_name
  }
}

resource "azurerm_mssql_database" "default" {
  name           = "main"
  server_id      = azurerm_mssql_server.default.id
  collation      = "SQL_Latin1_General_CP1_CI_AS"
  license_type   = "LicenseIncluded"
  max_size_gb    = 2
  sku_name       = "Basic"
  zone_redundant = false

  tags = {
    environment = var.environment_name
  }
}
