locals {
  service_account_token   = lookup(var.devops-cluster-svc-data, "token")
  service_account_ca_cert = lookup(var.devops-cluster-svc-data, "ca.crt")
}
