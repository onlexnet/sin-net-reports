variable "application_name" {
  type = string
}

variable "environment_name" {
  type = string
}

variable "resource_group" {
  type = object({
    id       = string
    location = string
    name     = string
  })
}

variable "python_version" {
  description = "Python runtime version for the Linux Function App"
  type        = string
  default     = "3.13"
}
