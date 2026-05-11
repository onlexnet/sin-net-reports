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
