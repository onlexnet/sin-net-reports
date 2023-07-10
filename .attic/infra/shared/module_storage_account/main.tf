resource "azurerm_storage_account" "default" {
  name                     = "${var.application_name}${var.environment_name}"
  resource_group_name      = var.resource_group.name
  location                 = var.resource_group.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

# Reports should be deleted ASAP - in Azure min is 1 day
resource "azurerm_storage_container" "reports" {
  name                  = "reports"
  storage_account_name  = azurerm_storage_account.default.name
  container_access_type = "private"
}

resource "azurerm_storage_management_policy" "short-living" {
  storage_account_id = azurerm_storage_account.default.id

  rule {
    name    = "delete-reports-after-1-day"
    enabled = true
    filters {
      prefix_match = [azurerm_storage_container.reports.name]
      blob_types   = ["blockBlob"]
    }
    actions {
      base_blob {
        delete_after_days_since_modification_greater_than = 100
      }
    }
  }
}
