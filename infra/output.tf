output "current_workspace_name" {
  value = terraform.workspace
}

variable "TFC_RUN_ID" {
  type    = string
  default = ""
}
