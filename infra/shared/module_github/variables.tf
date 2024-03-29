variable "environment_name" {
}

variable "azure_static_web_apps_api_token" {
  sensitive = true
}

variable "ONLEXNET_TENANT_ID" {
  sensitive = true
}

variable "ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID" {
  sensitive = true
}

variable "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_TIMEENTRIES" {
}

variable "ONLEXNET_SINNET_PRD01_CONTAINERAPP_NAME_WEBAPI" {
}

variable "ONLEXNET_INFRA_CLIENT_ID" {
  sensitive = true
}

variable "ONLEXNET_INFRA_SECRET" {
  sensitive = true
}
