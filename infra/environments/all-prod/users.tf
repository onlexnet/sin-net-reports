data "azurerm_aadb2c_directory" "example" {
  resource_group_name = "sinnet-app-manual"
  domain_name         = "sinnetapp.onmicrosoft.com"
}

provider "azuread" {
  alias     = "sinnetapp"
  tenant_id = data.azurerm_aadb2c_directory.example.tenant_id
}

# Not possible yet as https://github.com/hashicorp/terraform-provider-azuread/issues/175 is open
# data "azuread_user" "example" {
#   provider            = azuread.sinnetapp
#   user_principal_name = "test-operator-1@sinnetapp.onmicrosoft.com"
# }

# Access to development
data "azuread_group" "developers" {
  display_name     = "SinNet Developer"
  security_enabled = true
}

# Access to production
data "azuread_group" "support" {
  display_name     = "SinNet Support"
  security_enabled = true
}


