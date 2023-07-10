terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.61.0"
    }
  }

  required_version = ">= 0.15.3"

  backend "azurerm" {
    resource_group_name  = "onlexnet-default"
    storage_account_name = "onlexnetterraformbackend"

    container_name = "sinnet-prod"
    key            = "terraform.stg01.tfstate"
  }

}
