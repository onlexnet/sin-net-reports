locals {
    # Example values: westeurope, germanynorth
    # More examples: az account list-locations -o table --query '[].{Name:name}'
    # Used to construct some names where environment name is the part of constructed name
    # Used as param for some Azure services where location name is required
    environment_location = "westeurope" 

    organization_name = "onlexnet"
}
