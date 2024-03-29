provider "azurerm" {

  # By default, Terraform will attempt to register any Resource Providers that it supports,
  # even if they're not used in your configurations to be able to display more helpful error messages.
  # If you're running in an environment with restricted permissions, or wish to manage
  # Resource Provider Registration outside of Terraform you may wish to disable this flag;
  # however, please note that the error messages returned from Azure may be confusing as a result
  # (example: API version 2019-01-01 was not found for Microsoft.Foo).
  skip_provider_registration = true

  subscription_id = var.azure_subscription_id

  features {
    # skip_provider_registration = true # This is only required when the User, Service Principal, or Identity running Terraform lacks the permissions to register Azure Resource Providers.

    key_vault {

      # issue: when creating and destroying an environment, there is some competition between deleting access policy, and then of loosing access to read / delete secrets for e.g. SQL-ADMIN-PASSWORD secret.
      # solution from: https://stackoverflow.com/a/72506375/1237627
      recover_soft_deleted_key_vaults = false
      purge_soft_delete_on_destroy    = true

    }
  }
}
