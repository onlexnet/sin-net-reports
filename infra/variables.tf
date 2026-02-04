# impacts a lot of resources as a part of naming
# best practice: keep the same as folder name
variable "environment_name" {
  default = "prd01"
}

# Should be supplied by environment variables
variable "azure_subscription_id_prod" {
}

# GitHub App authentication variables
# These should be configured in Terraform Cloud workspace
# See shared/module_github/GITHUB_APP_SETUP.md for setup instructions
variable "github_app_id" {
  description = "GitHub App ID for Terraform authentication (app name: onlexnet-psa)"
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

# Some not-used environment variables just to hide Terraform Cloud warning like:
# │ Warning: Value for undeclared variable
# │ 
# │ The root module does not declare a variable named "GITHUB_TOKEN" but a
# │ value was found in file
# │ "/home/tfc-agent/.tfc-agent/component/terraform/runs/run-3TgMj9Pmtioz86nV/terraform.tfvars".
# │ If you meant to use this value, add a "variable" block to the
# │ configuration.
# │ 
# │ To silence these warnings, use TF_VAR_... environment variables to provide
# │ certain "global" settings to all configurations in your organization. To
# │ reduce the verbosity of these warnings, use the -compact-warnings option.

variable ARM_CLIENT_SECRET { // used by Azure provider
}
variable ARM_TENANT_ID { // used by Azure provider
}
variable ARM_CLIENT_ID { // used by Azure provider
}
variable CLOUDFLARE_API_TOKEN { // Cloudflare token named onlexnet-dns-edit to update DNS records in onlex.net
}
variable SINNETAPP_PROD_SECRET { // used by module_keyvault
}
variable CR_PAT { // used by module_keyvault
}
variable TF_VAR_azure_subscription_id_prod { // used by module_keyvault
}
