resource "azurerm_storage_account" "function" {
  name                     = lower("${var.application_name}${var.environment_name}report1sa")
  resource_group_name      = var.resource_group.name
  location                 = var.resource_group.location
  account_tier             = "Standard"
  account_replication_type = "LRS"

  tags = {
    environment = var.environment_name
  }
}

resource "azurerm_service_plan" "function" {
  name                = "${var.application_name}-${var.environment_name}-report1-plan"
  resource_group_name = var.resource_group.name
  location            = var.resource_group.location
  os_type             = "Linux"
  sku_name            = "FC1"

  tags = {
    environment = var.environment_name
  }
}

resource "azurerm_storage_container" "deployments" {
  name                  = "app-package-${lower("${var.application_name}${var.environment_name}report1")}"
  storage_account_id    = azurerm_storage_account.function.id
  container_access_type = "private"
}

# Container where generated reports (PDF/ZIP) are uploaded so a time-limited
# SAS link can be handed back to callers instead of streaming binaries.
resource "azurerm_storage_container" "reports" {
  name                  = "reports"
  storage_account_id    = azurerm_storage_account.function.id
  container_access_type = "private"
}

# Reports are transient: delete them automatically 7 days after creation so
# storage doesn't grow unbounded. The 24h SAS link expiry (enforced in
# fun_report1 code) is always shorter than this retention window.
resource "azurerm_storage_management_policy" "reports" {
  storage_account_id = azurerm_storage_account.function.id

  rule {
    name    = "expire-reports-after-7-days"
    enabled = true

    filters {
      blob_types   = ["blockBlob"]
      prefix_match = ["${azurerm_storage_container.reports.name}/"]
    }

    actions {
      base_blob {
        delete_after_days_since_creation_greater_than = 7
      }
    }
  }
}

# Shared secret used by webapi to authenticate calls to fun_report1's HTTP
# endpoints (checked against the X-Report1-Secret header). Generated once by
# Terraform and shared with webapi via module output.
resource "random_password" "shared_secret" {
  length  = 40
  special = false
}

resource "azurerm_role_assignment" "onlex_infra_storage" {
  scope                = azurerm_storage_account.function.id
  role_definition_name = "Storage Blob Data Contributor"
  principal_id         = data.azuread_service_principal.onlex_infra.object_id
}

data "azuread_service_principal" "onlex_infra" {
  display_name = "onlex-infra"
}

resource "azurerm_function_app_flex_consumption" "function" {
  name                = "${var.application_name}-${var.environment_name}-report1-fn"
  resource_group_name = var.resource_group.name
  location            = var.resource_group.location
  service_plan_id     = azurerm_service_plan.function.id

  runtime_name                = "python"
  runtime_version             = var.python_version
  storage_container_type      = "blobContainer"
  storage_container_endpoint  = "${azurerm_storage_account.function.primary_blob_endpoint}${azurerm_storage_container.deployments.name}"
  storage_authentication_type = "SystemAssignedIdentity"

  site_config {}

  app_settings = {
    "AzureWebJobsStorage__accountName" = azurerm_storage_account.function.name
    "REPORT1_SHARED_SECRET"            = random_password.shared_secret.result
    "REPORT1_STORAGE_ACCOUNT_NAME"     = azurerm_storage_account.function.name
    "REPORT1_STORAGE_CONTAINER_NAME"   = azurerm_storage_container.reports.name
  }

  identity {
    type = "SystemAssigned"
  }

  tags = {
    environment = var.environment_name
  }
}

# resource "azurerm_role_assignment" "function_storage_deployments" {
#   scope                = "${azurerm_storage_account.function.id}/blobServices/default/containers/${azurerm_storage_container.deployments.name}"
#   role_definition_name = "Storage Blob Data Contributor"
#   principal_id         = azurerm_function_app_flex_consumption.function.identity[0].principal_id

#   depends_on = [
#     azurerm_function_app_flex_consumption.function,
#   ]
# }

# Required for AzureWebJobsStorage with identity-based auth on Flex Consumption.
# Without account-scoped access, Kudu [StorageAccessibleCheck] fails during deploy
# with a misleading "MSITokenUnavailableException".
resource "azurerm_role_assignment" "function_storage_account" {
  scope                = azurerm_storage_account.function.id
  role_definition_name = "Storage Blob Data Owner"
  principal_id         = azurerm_function_app_flex_consumption.function.identity[0].principal_id

  depends_on = [
    azurerm_function_app_flex_consumption.function,
  ]
}

# Required to obtain a user delegation key (get_user_delegation_key) so the
# function can mint user-delegation SAS links for report downloads without
# ever handling a storage account key.
resource "azurerm_role_assignment" "function_storage_delegator" {
  scope                = azurerm_storage_account.function.id
  role_definition_name = "Storage Blob Delegator"
  principal_id         = azurerm_function_app_flex_consumption.function.identity[0].principal_id

  depends_on = [
    azurerm_function_app_flex_consumption.function,
  ]
}
