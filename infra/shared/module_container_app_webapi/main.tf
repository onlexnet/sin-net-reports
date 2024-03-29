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

  lifecycle {
    ignore_changes = [
      template[0].container[0].image
    ]
  }

  secret {
    name = "applicationinsights-connection-string"
    value = var.env.APPLICATIONINSIGHTS_CONNECTION_STRING
  }

  secret {
    # autogenerated by manual integration with sinnet-b2c
    # should be updated manually
    name = "sinnet-b2c-authentication-secret"
    value = var.env.SINNETAPP_PROD_SECRET
  }

  secret {
    name  = "cr-pat"
    value = var.env.CR_PAT
  }

  secret {
    # not used, should be removed
    # webapi does not use database
    name  = "database-host"
    value = var.env.DATABASE_HOST
  }

  secret {
    # not used, should be removed
    # webapi does not use database
    name  = "database-name"
    value = var.env.DATABASE_NAME
  }

  secret {
    # not used, should be removed
    # webapi does not use database
    name  = "database-password"
    value = var.env.DATABASE_PASSWORD
  }

  secret {
    # not used, should be removed
    # webapi does not use database
    name  = "database-port"
    value = var.env.DATABASE_PORT
  }

  secret {
    # not used, should be removed
    # webapi does not use database
    name  = "database-username"
    value = var.env.DATABASE_USERNAME
  }

  secret {
    # not used, should be removed
    # but can't be removed per https://github.com/microsoft/azure-container-apps/issues/395
    name  = "github-token"
    value = var.env.CR_PAT
  }

  dapr {
    app_id       = "uservice-webapi"
    app_port     = "8080"
    app_protocol = "http"
  }

  template {

    min_replicas = 1
    max_replicas = 1

    container {

      name   = "uservice-webapi"
      image  = "ghcr.io/onlexnet/uservice-webapi:latest"
      cpu    = 0.5
      memory = "1Gi"

      # scale - currently not supported
      # https://github.com/hashicorp/terraform-provider-azurerm/issues/20629
      # please manage manually using portal or az tools

      env {
        name = "APPLICATIONINSIGHTS_CONNECTION_STRING"
        secret_name = "applicationinsights-connection-string"
      }

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
