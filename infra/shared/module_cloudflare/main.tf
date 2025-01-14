# Configure the Cloudflare provider using the required_providers stanza
# required with Terraform 0.13 and beyond. You may optionally use version
# directive to prevent breaking changes occurring unannounced.
terraform {
  required_providers {
    cloudflare = {
      source  = "cloudflare/cloudflare"
      version = "~> 3.0"
    }
  }
}

data "cloudflare_zone" "onlexnet" {
  name = "onlex.net"
}

resource "cloudflare_record" "webapi" {
  zone_id = data.cloudflare_zone.onlexnet.zone_id
  name    = var.webapi_prefix
  value   = var.webapi_fqdn
  type    = "CNAME"
  proxied = false
  ttl     = 300
}

resource "cloudflare_record" "webapp_prod" {
  zone_id = data.cloudflare_zone.onlexnet.zone_id
  name    = var.webapp_prefix_prod
  value = var.webapp_fqdn_prod
  type    = "CNAME"
  ttl     = 300
}

resource "cloudflare_record" "webapp_test" {
  zone_id = data.cloudflare_zone.onlexnet.zone_id
  name    = var.webapp_prefix_test
  value = var.webapp_fqdn_test
  type    = "CNAME"
  ttl     = 300
}
