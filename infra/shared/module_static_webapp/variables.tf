variable resource_group {
  type = object({
    location = string
    name     = string
  })
}

variable custom_domain {
}

variable subdomain {
  description = "Will be used as intenral name for web_app resource"
}
