resource "azurerm_storage_account" "default" {
  name                     = "${var.application_name}${var.environment_name}"
  resource_group_name      = var.resource_group.name
  location                 = var.resource_group.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_container" "short-living" {
  name                  = "short-living"
  storage_account_name  = azurerm_storage_account.default.name
  container_access_type = "private"
}

resource "azurerm_storage_management_policy" "short-living" {
  storage_account_id = azurerm_storage_account.default.id

  rule {
    name    = "delete-after-1-day"
    enabled = true
    filters {
      blob_types = ["blockBlob"]
      match_blob_index_tag {
        name      = "DELETE_AFTER_DAYS"
        operation = "=="
        value     = "1"
      }
    }
    actions {
      base_blob {
        delete_after_days_since_modification_greater_than = 100
      }
    }
  }
}
