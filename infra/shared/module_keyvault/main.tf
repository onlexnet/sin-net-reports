data "azuread_group" "support" {
  display_name     = "${var.application_name}-${var.environment_name}-support"
  security_enabled = true
}

resource "random_id" "id" {
  byte_length = 8
}

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "example" {
  // name                       = "${var.application_name}_${var.environment_name}_${random_id.id.hex}"
  name                       = "${var.organization_name}-${var.application_name}-${var.environment_name}"
  location                   = var.resourcegroup.location
  resource_group_name        = var.resourcegroup.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "standard"
  soft_delete_retention_days = 7

  access_policy {
    tenant_id    = data.azurerm_client_config.current.tenant_id
    object_id    = data.azuread_group.support.object_id

    secret_permissions = [
      # "Get", "Set", "List", "Delete"
      "Get", "List"
    ]
  
  }

  access_policy {
    tenant_id    = data.azurerm_client_config.current.tenant_id
    object_id    = data.azurerm_client_config.current.object_id

    secret_permissions = [
      # all known values, as there is no point to limit onlex-infra, and additionally lack of some values throw unexpected issues (e.g. in destroying keyvault)
      "Backup", "Delete", "Get", "List", "Purge", "Recover", "Restore", "Set"
    ]
  }

}

resource "azurerm_key_vault_secret" "sqladminpassword" {
  name         = "SQL-ADMIN-PASSWORD"
  value        = random_string.password.result
  key_vault_id = azurerm_key_vault.example.id
}

# Run the script to get the environment variables of interest.
# This is a data source, so it will run at plan time.
data "external" "env" {
  program = ["${path.module}/env.sh"]

  # For Windows (or Powershell core on MacOS and Linux),
  # run a Powershell script instead
  #program = ["${path.module}/env.ps1"]
}

resource "random_string" "password" {
  length  = 32
  special = true
}


