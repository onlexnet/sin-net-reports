output "namespace_name" {
  value = local.namespace_name
}

output "devops-cluster-svc-data" {
    value = data.kubernetes_secret.devops-cluster-svc.data
}
