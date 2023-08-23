resource "azurerm_mssql_server" "default" {
  name                         = "${var.application_name}-${var.environment_name}"
  resource_group_name          = var.resource_group.name
  location                     = var.resource_group.location
  version                      = "12.0"
  administrator_login          = var.application_name
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

# enable access to sqlserver
# https://community.fabric.microsoft.com/t5/Service/Client-with-IP-address-xx-xx-xxx-xxx-is-not-allowed-to-access/m-p/2028409
resource "azurerm_mssql_firewall_rule" "example" {
  name      = "FirewallRule1"
  server_id = azurerm_mssql_server.default.id
  # The Azure feature Allow access to Azure services can be enabled by setting start_ip_address and end_ip_address to 0.0.0.0 which (is documented in the Azure API Docs).
  # https://learn.microsoft.com/en-us/rest/api/sql/2022-08-01-preview/firewall-rules/create-or-update?tabs=HTTP
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}
