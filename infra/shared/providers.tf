provider "azurerm" {

  subscription_id = var.azure_subscription_id

  features {

    key_vault {

      # issue: when creating and destroying an environment, there is some competition between deleting access policy, and then of loosing access to read / delete secrets for e.g. SQL-ADMIN-PASSWORD secret.
      # solution from: https://stackoverflow.com/a/72506375/1237627
      recover_soft_deleted_key_vaults = false
      purge_soft_delete_on_destroy    = true

    }
  }
}
