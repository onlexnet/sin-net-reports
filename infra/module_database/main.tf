terraform {
  required_providers {
    postgresql = {
      source = "cyrilgdn/postgresql"
    }
  }
  required_version = ">= 0.13"
}

provider "postgresql" {
  // because database is (by design) secured on remote VM
  // we assume port 5432 is already redirected from database VM
  // ssh -L 5432:localhost:5432 <USERNAME>@raport.sin.net.pl
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
  name     = "uservice_customers_${var.environment_name}"
  login    = true
  password = random_id.id.hex
}

resource "postgresql_database" "customers" {
  name              = "customers_${var.environment_name}"
  owner             = postgresql_role.uservice_customers.name
  template          = "template0"
  lc_collate        = "C"
  connection_limit  = -1
  allow_connections = true
}