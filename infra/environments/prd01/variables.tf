# shared names reused across environments
variable "psql_infrauser_name" {}
variable "psql_infrauser_password" {}
variable "sinnet_k8s_host" {}
variable "sinnet_k8s_token" {}

# customized name so that multiple subscription ids may be defined in one file
variable "sinnet_prod_subscription_id" {}
variable "onlexnet_sinnet_prd01_subscription_id" {}

variable "onlex_sinnet_azdo_personal_token" {}
variable "onlex_sinnet_azdo_service_url" {}
