# in the future we would like to get such data directly from Azure Vault, but right now
# sharing them using terraform is the simplest option
output "env" {
  value = {
    NORDIGEN_SECRET_ID = azurerm_key_vault_secret.NORDIGEN-SECRET-ID.value
    NORDIGEN_SECRET_KEY = azurerm_key_vault_secret.NORDIGEN-SECRET-KEY.value
    SQL_ADMIN_PASSWORD = random_string.password.result
  }
  sensitive = true
}
