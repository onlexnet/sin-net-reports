data "azurerm_aadb2c_directory" "example" {
  resource_group_name = "sinnet-app-manual"
  domain_name         = "sinnetapp.onmicrosoft.com"
}

provider "azuread" {
  alias     = "sinnetapp"
  tenant_id = data.azurerm_aadb2c_directory.example.tenant_id
}

# Not possible yet 
# Try when new version of the provider is created
# resource "azuread_user" "example" {
#   provider                    = azuread.sinnetapp
#   user_principal_name         = "test-operator-10@sinnetapp.onmicrosoft.com"

#   disable_password_expiration = true
#   # user_principal_name = "jdoe@hashicorp.com"
#   display_name  = "J. Doe"
#   mail_nickname = "jdoe"
#   password      = "SecretP@sswd99!"
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


