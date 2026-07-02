variable "webapi_fqdn" {
  description = "original full address where the application is located"
}
variable "webapi_prefix" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "webapp_fqdn_time" {
  description = "original full address where the application is located"
}

variable "webapp_prefix_time" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "report1_fqdn" {
  description = "original full address where the application is located"
}

variable "report1_prefix" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "report1_domain_verification_id" {
  description = "custom domain verification ID from Azure Function App for TXT record"
}
