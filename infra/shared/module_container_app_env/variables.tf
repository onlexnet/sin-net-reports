variable "resource_group" {
  type = object({
    id       = string
    location = string
    name     = string
  })
}

variable "log_analytics_workspace" {
  type = object({
    id = string
  })
}
