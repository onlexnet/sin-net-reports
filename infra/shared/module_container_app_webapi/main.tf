resource "azurerm_container_app" "default" {
  name                         = "sinnet-webapi"
  container_app_environment_id = var.env_id
  resource_group_name          = var.resource_group.name
  revision_mode                = "Single"

  registry {
    server               = "ghcr.io"
    username             = var.env.GITHUB_USERNAME
    password_secret_name = "cr-pat"
  }

  # step 2
  ingress {
    external_enabled = true
    target_port      = 8080
    traffic_weight {
      latest_revision = true
      percentage      = 100
    }
  }

  secret {
    name  = "cr-pat"
    value = var.env.CR_PAT
  }

  secret {
    # not used, should be removed
    # but can't be removed per https://github.com/microsoft/azure-container-apps/issues/395
    name  = "github-token"
    value = var.env.CR_PAT
  }

  secret {
    name  = "database-host"
    value = var.env.DATABASE_HOST
  }

  secret {
    name  = "database-host"
    value = var.env.DATABASE_HOST
  }

  secret {
    name  = "database-port"
    value = var.env.DATABASE_PORT
  }

  secret {
    name  = "database-name"
    value = var.env.DATABASE_NAME
  }

  secret {
    name  = "database-password"
    value = var.env.DATABASE_PASSWORD
  }

  secret {
    name  = "database-username"
    value = var.env.DATABASE_USERNAME
  }

  dapr {
    app_id       = "uservice-webapi"
    app_port     = "8080"
    app_protocol = "http"
  }

  lifecycle {
    ignore_changes = [template[0].container[0].image]
  }

  template {

    container {

      name   = "uservice-webapi"
      image  = "ghcr.io/onlexnet/uservice-webapi:latest"
      cpu    = 0.25
      memory = "0.5Gi"

      # scale - currently not supported
      # https://github.com/hashicorp/terraform-provider-azurerm/issues/20629
      # please manage manually using portal or az tools

      env {
        name        = "DATABASE_HOST"
        secret_name = "database-host"
      }

      env {
        name        = "DATABASE_PORT"
        secret_name = "database-port"
      }

      env {
        name        = "DATABASE_NAME"
        secret_name = "database-name"
      }

      env {
        name        = "DATABASE_USERNAME"
        secret_name = "database-username"
      }

      env {
        name        = "DATABASE_PASSWORD"
        secret_name = "database-password"
      }

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }

      env {
        # default value is BPL_JVM_THREAD_COUNT=250 and app docker image build by packeto cant start as calculated memry is higher than available memory (0.5GB atm)
        # more: https://github.com/paketo-buildpacks/bellsoft-liberica/issues/68
        name  = "BPL_JVM_THREAD_COUNT"
        value = "20"
      }
    }
  }
}
