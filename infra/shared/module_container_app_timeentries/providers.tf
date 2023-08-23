# We strongly recommend using the required_providers block to set the
# Azure Provider source and version being used
terraform {
  required_providers {
    azapi = {
      source = "Azure/azapi"
    }
  }
}
