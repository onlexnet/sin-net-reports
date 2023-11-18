variable "resource_group" {
  type = object({
    location = string
    name     = string
  })
}

variable "workspace_id" {
}
