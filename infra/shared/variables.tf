# Set of variables used to create / update environment on Azure

# Variables: environment_name, environment_location and application_name cane be provided using vars file e. prd.vars
# And executed: terraform apply -var-file prd.tfvars

variable "environment_name" {
  # Example values: prd, stg, sit, dev
  # Used to construct some names where environment name is the part of constructed name
  type = string
}

# GitHub App authentication variables for managing GitHub resources
# See module_github/GITHUB_APP_SETUP.md for setup instructions
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

# # Name of predefined superuser on external postgres server where all
# # databases are created. In such small project we use single SQL server with multiple databases / schemas.
# variable "psql_infrauser_name" {
# }
# # Password, as sensitive information, should be provided as env variable
# variable "psql_infrauser_password" {}

# variable "environment_location" {
#   # Example values: westeurope, germanynorth
#   # More examples: az account list-locations -o table --query '[].{Name:name}'
#   # Used to construct some names where environment name is the part of constructed name
#   # Used as param for some Azure services where location name is required
#   type    = string
#   default = "westeurope"
# }

variable "application_name" {
  type = string
}

variable "azure_subscription_id" {
  type = string
}