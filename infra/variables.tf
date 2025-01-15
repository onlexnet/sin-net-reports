# impacts a lot of resources as a part of naming
# best practice: keep the same as folder name
variable "environment_name" {
  default = "prd01"
}

# Should be supplied by environment variables
variable "azure_subscription_id_prod" {
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

variable GITHUB_TOKEN { // used by Github provider
}
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
