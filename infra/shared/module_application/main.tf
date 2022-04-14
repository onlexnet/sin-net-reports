# Create an application
# resource "azuread_application" "default" {
#   name = "${var.application_name}-${var.environment_name}"
#   reply_urls = [
#     "http://localhost",
#     "http://localhost:3000",
#     "https://raport.sin.net.pl"
#   ]
# }

# # Create a service principal
# resource "azuread_service_principal" "default" {
#   application_id = azuread_application.default.application_id
# }


