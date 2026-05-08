output "webapi_fqdn" {
  description = "The fully qualified domain name of the WebAPI container app"
  value       = module.container_apps_webapi.webapi_fqdn
}

output "webapi_url" {
  description = "The complete URL of the WebAPI service (https://webapi_fqdn)"
  value       = "https://${module.container_apps_webapi.webapi_fqdn}"
}

output "function_report1_fqdn" {
  description = "The default hostname of the report1 Function App"
  value       = module.fun_report1.function_app_fqdn
}

output "report1_url" {
  description = "The public URL of report1 (via Cloudflare)"
  value       = "https://report1.onlex.net"
}
