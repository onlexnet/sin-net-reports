# impacts a lot of resources as a part of naming
# best practice: keep the same as folder name
variable "environment_name" {
  default = "dev01"
}

# Should be supplied by environment variables
variable "azure_subscription_id_dev" {
}
