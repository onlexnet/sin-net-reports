resource "azurerm_dns_zone" "default" {
  name                = "dev.sinnet.siudek.net"
  resource_group_name = var.resource_group_name
}