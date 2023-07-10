# Set of variables used to create / update environment on Azure

# Variables: environment_name, environment_location and application_name cane be provided using vars file e. prd.vars
# And executed: terraform apply -var-file prd.tfvars

variable "environment_name" {
  # Example values: prd, stg, sit, dev
  # Used to construct some names where environment name is the part of constructed name
  type = string
}

# Name of predefined superuser on external postgres server where all
# databases are created. In such small project we use single SQL server with multiple databases / schemas.
variable "psql_infrauser_name" {
}
# Password, as sensitive information, should be provided as env variable
variable "psql_infrauser_password" {}

variable "environment_location" {
  # Example values: westeurope, germanynorth
  # More examples: az account list-locations -o table --query '[].{Name:name}'
  # Used to construct some names where environment name is the part of constructed name
  # Used as param for some Azure services where location name is required
  type    = string
  default = "westeurope"
}

variable "application_name" {
  type    = string
  default = "sinnet"
}

# Target subscription used to create all resources defined by the Terraform scripts
# please fullfil variables as below using standard Terraform approaches (vars file, var param of env variables)
variable "env_subscription_id" {
  type      = string
  sensitive = true
}

# Subscription used to acces shared resources used by SinNet application
# Generally all non-prod envs shares some subscription, and prod envs share some common subscription
# In my small project probably prod and non-prod shares the same subscription
# It allow to keep e.g. shared DNS or shared Container REgistry
variable "shared_subscription_id" {
  type      = string
  sensitive = true
}

# One environment - called 'sinnet k8s' is managed outside of Azure
# so additional configuration is required for jkubernetes provider
# please fullfil variables as below using standard Terraform approaches (vars file, var param of env variables)
variable "sinnet_k8s_host" {}
variable "sinnet_k8s_token" {}

variable "onlex_sinnet_azdo_service_url" {}
variable "onlex_sinnet_azdo_personal_token" {}

variable "support_security_group" {}
