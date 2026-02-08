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

variable "env_id" {}

variable "env" {
  type = object({
    APPLICATIONINSIGHTS_CONNECTION_STRING = string
    GITHUB_USERNAME                       = string
    CR_PAT                                = string
    DATABASE_HOST                         = string
    DATABASE_NAME                         = string
    DATABASE_PORT                         = string
    DATABASE_USERNAME                     = string
    DATABASE_PASSWORD                     = string
  })
}

variable "applicationinsights_agent_version" {
  type        = string
  description = "Version of Application Insights Java agent to download"
  default     = "3.7.6"
}
