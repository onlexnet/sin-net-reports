module "shared" {
  # Shared part of environments
  application_name        = "sinnet"
  source                  = "../../shared"
  psql_infrauser_name     = var.psql_infrauser_name
  psql_infrauser_password = var.psql_infrauser_password
  sinnet_k8s_host         = var.sinnet_k8s_host
  sinnet_k8s_token        = var.sinnet_k8s_token

  # different value for prod and non-prod environments
  shared_subscription_id = var.sinnet_prod_subscription_id

  # customized part
  environment_name    = "stg01"
  env_subscription_id = var.onlexnet_sinnet_stg01_subscription_id

  onlex_sinnet_azdo_service_url    = var.onlex_sinnet_azdo_service_url
  onlex_sinnet_azdo_personal_token = var.onlex_sinnet_azdo_personal_token
  support_security_group           = var.support_security_group
}
