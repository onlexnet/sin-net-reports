terraform {
  required_providers {
    azuredevops = {
      source  = "microsoft/azuredevops"
      version = ">=0.1.0"
    }
  }
}

locals {
  apiserver_url = "https://raport.sin.net.pl:16443"
}