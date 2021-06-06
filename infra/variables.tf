# Set of variables used to create / update environment on Azure

# Variables: environment_name, environment_location and application_name cane be provided using vars file e. prd.vars
# And executed: terraform apply -var-file prd.tfvars

variable environment_name {
  # Example values: prd, stg, sit, dev
  # Used to construct some names where environment name is the part of constructed name
  type = string
}

variable environment_location {
  # Example values: westeurope, germanynorth
  # More examples: az account list-locations -o table --query '[].{Name:name}'
  # Used to construct some names where environment name is the part of constructed name
  # Used as param for some Azure services where location name is required
  type = string
  default = "westeurope"
}

variable application_name {
  type = string
  default = "sinnet"
}

# Target subscription used to create all resources defined by the Terraform scripts
# please fullfil variables as below using standard Terraform approaches (vars file, var param of env variables)
variable subscription_id {
  type = string
  sensitive=true
}

# One environment - called 'sinnet k8s' is managed outside of Azure
# so additional configuration is required for jkubernetes provider
# please fullfil variables as below using standard Terraform approaches (vars file, var param of env variables)
variable sinnet_k8s_host {}
variable sinnet_k8s_token {}


