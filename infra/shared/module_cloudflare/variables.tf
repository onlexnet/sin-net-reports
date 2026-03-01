variable "webapp_fqdn_prod" {
  description = "original full address where the application is located"
}
variable "webapp_prefix_prod" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "webapp_time_fqdn_prod" {
  description = "original full address where the time webapp is located"
}
variable "webapp_time_prefix_prod" {
  description = "subdomain part of DNS name for time webapp, eg. 'sinnet-time', where the final value is sinnet-time.onlex.net"
}

variable "webapi_fqdn" {
  description = "original full address where the application is located"
}
variable "webapi_prefix" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}
