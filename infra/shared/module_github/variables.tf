variable "environment_name" {
}

variable "azure_static_web_apps_api_token" {
  sensitive = true
}

variable "ONLEXNET_TENANT_ID" {
  sensitive = true
}

variable "ONLEXNET_SINNET_DEV01_SUBSCRIPTION_ID" {
  sensitive = true
}

variable "ONLEXNET_SINNET_DEV01_CONTAINERAPP_NAME" {
}

variable "ONLEXNET_INFRA_CLIENT_ID" {
  sensitive = true
}

variable "ONLEXNET_INFRA_SECRET" {
  sensitive = true
}
