resource "azurerm_static_web_app" "webapp" {
  name                = var.name
  resource_group_name = var.resource_group.name
  location            = var.resource_group.location

  lifecycle {
    ignore_changes = [repository_branch, repository_url]
  }
}
