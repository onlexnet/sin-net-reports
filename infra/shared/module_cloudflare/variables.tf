variable "webapp_fqdn_prod" {
  description = "original full address where the application is located"
}
variable "webapp_prefix_prod" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}

variable "webapp_fqdn_test" {
  description = "original full address where the test version of the application is located"
}
variable "webapp_prefix_test" {
  description = "subdomain part of DNS name, eg. 'abc-test', where the final value is abc-test.onlex.net"
}

variable "webapi_fqdn" {
  description = "original full address where the application is located"
}
variable "webapi_prefix" {
  description = "subdomain part of DNS name, eg. 'abc', where the final value is abc.onlex.net"
}
