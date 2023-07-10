variable "k8s_host" {}
variable "k8s_token" {}

variable "secret_appinsight_connection_string" {
  type = string
}

variable "config_services_database_name" {}
variable "config_services_database_username" {}
variable "config_services_database_password" {}
variable "config_reports_storage_address" {}
variable "config_reports_container_name" {}

variable "environment_name" {
  type = string
}

variable "docker_registry_username" {}
variable "docker_registry_password" {}
variable "docker_registry_server" {}

variable "namespace_name_system" {}


