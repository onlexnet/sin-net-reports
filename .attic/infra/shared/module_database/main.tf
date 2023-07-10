terraform {
  required_providers {
    postgresql = {
      source = "cyrilgdn/postgresql"
    }
  }
  required_version = ">= 0.13"
}

provider "postgresql" {
  host            = "localhost"
  port            = 5432
  username        = var.db_user_name
  password        = var.db_user_password
  sslmode         = "require"
  connect_timeout = 15
}

resource "random_id" "id" {
  byte_length = 8
}

resource "postgresql_role" "uservice_customers" {
  name     = "onlexnet_sinnet_${var.environment_name}_customers"
  login    = true
  password = random_id.id.hex
}

resource "postgresql_database" "customers" {
  name              = postgresql_role.uservice_customers.name
  owner             = postgresql_role.uservice_customers.name
  template          = "template0"
  lc_collate        = "C"
  connection_limit  = -1
  allow_connections = true
}

resource "random_id" "services_owner" {
  byte_length = 8
}

resource "postgresql_role" "services_owner" {
  name     = "onlexnet_sinnet_${var.environment_name}_services"
  login    = true
  password = random_id.services_owner.hex
}

resource "postgresql_database" "services" {
  name              = postgresql_role.services_owner.name
  owner             = postgresql_role.services_owner.name
  template          = "template0"
  lc_collate        = "C"
  connection_limit  = -1
  allow_connections = true
}
