variable "webapp_fqdn_prod" {
  description = "original full address where the application is located"
}
variable "webapp_prefix_prod" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "webapi_fqdn" {
  description = "original full address where the application is located"
}
variable "webapi_prefix" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}
