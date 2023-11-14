locals {
  application_name = "sinnet"
}

module "shared" {
  source                = "./shared"
  environment_name      = var.environment_name
  application_name      = local.application_name
  azure_subscription_id = var.azure_subscription_id_prod
}


