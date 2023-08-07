# in the future we would like to get such data directly from Azure Vault, but right now
# sharing them using terraform is the simplest option
output "env" {
  value = {
    SQL_ADMIN_PASSWORD = azurerm_key_vault_secret.sqladminpassword.value
    # random_string.password.result
  }
  sensitive = true
}