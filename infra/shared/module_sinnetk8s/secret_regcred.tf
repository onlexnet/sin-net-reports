# Solution based on https://stackoverflow.com/questions/62137632/create-kubernetes-secret-for-docker-registry-terraform
# Name regcred is well-known and used in HELM charts

resource "kubernetes_secret" "docker-registry" {
  metadata {
    name      = "regcred"
    namespace = local.namespace_name
  }

  data = {
    ".dockerconfigjson" = "${data.template_file.docker_config_script.rendered}"
  }

  type = "kubernetes.io/dockerconfigjson"

  depends_on = [
    kubernetes_namespace.default
  ]
}


data "template_file" "docker_config_script" {
  template = file("${path.module}/config.json")
  vars = {
    docker-username = var.docker_registry_username
    docker-password = var.docker_registry_password
    docker-server   = var.docker_registry_server
    auth            = base64encode("${var.docker_registry_username}:${var.docker_registry_password}")
  }
}