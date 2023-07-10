data "azurerm_client_config" "current" {}

resource "azurerm_resource_group" "onlex-env-all-prod" {
  name     = "onlex-env-all-prod"
  location = "West Europe"
}

# single place to keep shared across prod env variables
# 
resource "azurerm_key_vault" "onlex-sinnet-prod" {
  name                        = "onlex-sinnet-prod"
  location                    = azurerm_resource_group.onlex-env-all-prod.location
  resource_group_name         = azurerm_resource_group.onlex-env-all-prod.name
  enabled_for_disk_encryption = true
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days  = 7
  purge_protection_enabled    = false

  sku_name = "standard"

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    key_permissions = [
      "Get", "Create" , "Update"
    ]

    secret_permissions = [
      "Get", "Set"
    ]
  }

  # Access for SinNet support
  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azuread_group.support.object_id

    key_permissions = [
      "Get", "Create" , "Update", "List", "Delete"
    ]

    secret_permissions = [
      "Get", "Set", "List", "Delete"
    ]
  }

}
