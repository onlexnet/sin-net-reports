# in the future we would like to get such data directly from Azure Vault, but right now
# sharing them using terraform is the simplest option
output "env" {
  value = {
    SQL_ADMIN_PASSWORD                    = azurerm_key_vault_secret.sqladminpassword.value
    GITHUB_USERNAME                       = "siudeks"
    CR_PAT                                = data.external.env.result["CR_PAT"],
    ONLEXNET_TENANT_ID                    = data.external.env.result["ONLEXNET_TENANT_ID"],
    ONLEXNET_SINNET_DEV01_SUBSCRIPTION_ID = data.external.env.result["ONLEXNET_SINNET_DEV01_SUBSCRIPTION_ID"],
    ONLEXNET_INFRA_CLIENT_ID              = data.external.env.result["ONLEXNET_INFRA_CLIENT_ID"],
    ONLEXNET_INFRA_SECRET                 = data.external.env.result["ONLEXNET_INFRA_SECRET"],
    SINNETAPP_PROD_SECRET                 = data.external.env.result["SINNETAPP_PROD_SECRET"]
  }
  sensitive = true
}

