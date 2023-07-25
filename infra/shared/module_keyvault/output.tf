# in the future we would like to get such data directly from Azure Vault, but right now
# sharing them using terraform is the simplest option
output "env" {
  value = {
    SQL_ADMIN_PASSWORD = random_string.password.result
  }
  sensitive = true
}
