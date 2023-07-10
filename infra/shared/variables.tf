# Set of variables used to create / update environment on Azure

# Variables: environment_name, environment_location and application_name cane be provided using vars file e. prd.vars
# And executed: terraform apply -var-file prd.tfvars

variable "environment_name" {
  # Example values: prd, stg, sit, dev
  # Used to construct some names where environment name is the part of constructed name
  type = string
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
  type    = string
}

variable "azure_subscription_id" {
  type = string
}