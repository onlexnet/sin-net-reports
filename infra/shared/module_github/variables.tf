# GitHub App authentication variables
# See GITHUB_APP_SETUP.md for instructions on how to obtain these values
variable "github_app_id" {
  description = "GitHub App ID for Terraform authentication"
  type        = string
}

variable "github_app_installation_id" {
  description = "GitHub App Installation ID"
  type        = string
}

variable "github_app_pem" {
  description = "GitHub App private key in PEM format (content, not path)"
  type        = string
  sensitive   = true
}

variable "environment_name" {
}

variable "webapp_prod_api_token" {
  sensitive = true
}

variable "webapp_test_api_token" {
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

variable "APPLICATIONINSIGHTS_CONNECTION_STRING" {
  sensitive = true
}
