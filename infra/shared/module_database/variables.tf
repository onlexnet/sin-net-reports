variable "resource_group" {
  type = object({
    location = string
    name     = string
  })
}

variable "admin_password" {
  sensitive = true
}

variable "environment_name" {
  # Example values: prd, stg, sit, dev
  # Used to construct some names where environment name is the part of constructed name
  type = string
}
