data "azuread_group" "support" {
  display_name     = var.support_security_group_name
  security_enabled = true
}

resource "random_id" "id" {
  byte_length = 8
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "example" {
  // name                       = "${var.application_name}_${var.environment_name}_${random_id.id.hex}"
  name                       = "${var.application_name}-${var.environment_name}"
  location                   = var.resourcegroup.location
  resource_group_name        = var.resourcegroup.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "standard"
  soft_delete_retention_days = 7
}

resource "azurerm_key_vault_access_policy" "support" {
  key_vault_id = azurerm_key_vault.example.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azuread_group.support.object_id

  secret_permissions = [
    "Get", "Set", "List", "Delete"
  ]
}

resource "azurerm_key_vault_access_policy" "infra" {
  key_vault_id = azurerm_key_vault.example.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azurerm_client_config.current.object_id

  secret_permissions = [
    "Get", "Set", "List", "Delete"
  ]
}

resource "azurerm_key_vault_secret" "appinsight_connection_string" {
  name         = "appinsight-connection-string"
  value        = var.appinsight_connection_string
  key_vault_id = azurerm_key_vault.example.id
}
