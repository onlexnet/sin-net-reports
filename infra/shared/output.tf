output "webapi_fqdn" {
  description = "The fully qualified domain name of the WebAPI container app"
  value       = module.container_apps_webapi.webapi_fqdn
}

output "webapi_url" {
  description = "The complete URL of the WebAPI service (https://webapi_fqdn)"
  value       = "https://${module.container_apps_webapi.webapi_fqdn}"
}
