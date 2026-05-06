output "current_workspace_name" {
  value = terraform.workspace
}

output "report1_url" {
  value = module.shared.report1_url
}

variable "TFC_RUN_ID" {
  type    = string
  default = ""
}
