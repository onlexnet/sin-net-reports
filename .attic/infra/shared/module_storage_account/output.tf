output "reports_storage_address" {
  value = azurerm_storage_account.default.primary_blob_endpoint
}

output "reports_container_name" {
  value = azurerm_storage_container.reports.name
}
