output "devops-cluster-svc-token" {
  value = lookup(data.kubernetes_secret.devops-cluster-svc.data, "token")
}

output "ca-cert" {
  value = lookup(data.kubernetes_secret.devops-cluster-svc.data, "ca.crt")
}
