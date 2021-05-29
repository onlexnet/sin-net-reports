variable environment_name {
}

variable environment_location {
}

variable application_name {
}

# for security reason value of the variable is not hardcoded in
# local files and should be provided from command line
variable subscription_id {
  sensitive=true
}
