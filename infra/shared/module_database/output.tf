output database_host {
  value = azurerm_mssql_server.default.fully_qualified_domain_name
}

output database_port {
  value = "1433" # TODO find valud from terraform
}

output database_username {
  value = azurerm_mssql_server.default.administrator_login
}

output database_password {
  value = azurerm_mssql_server.default.administrator_login_password
}

output database_name {
  value = azurerm_mssql_database.default.name
}

