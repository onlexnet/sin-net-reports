provider kubernetes {
  
  host = var.sinnet_k8s_host

  client_certificate     = "${file("~/onlex_infra.crt")}"
  client_key             = "${file("~/onlex_infra.key")}"
  insecure = true
}