output "devops-cluster-svc-token" {
  # sensitive = true
  value     = lookup(data.kubernetes_secret.devops-cluster-svc.data, "token")
}

output "ca-cert" {
  value = lookup(data.kubernetes_secret.devops-cluster-svc.data, "ca.crt")
}
