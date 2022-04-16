terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=3.0.0"
    }
    azuredevops = {
      source  = "microsoft/azuredevops"
      version = ">=0.1.0"
    }

    azuread = {
      source = "hashicorp/azuread"
      version = "2.20.0"
    }
  }

  required_version = ">= 0.15.3"

  backend "azurerm" {
    resource_group_name  = "onlexnet-default"
    storage_account_name = "onlexnetterraformbackend"

    container_name = "sinnet-prod"
    key            = "terraform.all.tfstate"
  }

}
