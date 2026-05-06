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
  sku_name            = "Y1"

  tags = {
    environment = var.environment_name
  }
}

resource "azurerm_linux_function_app" "function" {
  name                       = "${var.application_name}-${var.environment_name}-report1-fn"
  resource_group_name        = var.resource_group.name
  location                   = var.resource_group.location
  service_plan_id            = azurerm_service_plan.function.id
  storage_account_name       = azurerm_storage_account.function.name
  storage_account_access_key = azurerm_storage_account.function.primary_access_key
  https_only                 = true

  functions_extension_version = "~4"

  identity {
    type = "SystemAssigned"
  }

  site_config {
    application_stack {
      python_version = var.python_version
    }

    ftps_state = "Disabled"
  }

  app_settings = {
    FUNCTIONS_WORKER_RUNTIME       = "python"
    FUNCTIONS_EXTENSION_VERSION    = "~4"
    WEBSITE_RUN_FROM_PACKAGE       = "1"
    SCM_DO_BUILD_DURING_DEPLOYMENT = "false"
  }

  tags = {
    environment = var.environment_name
  }
}
