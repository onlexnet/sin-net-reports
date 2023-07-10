output "services_database_name" {
  value = postgresql_database.services.name
}

output "services_database_username" {
  value = postgresql_role.services_owner.name
}

output "services_database_password" {
  value = postgresql_role.services_owner.password
}
