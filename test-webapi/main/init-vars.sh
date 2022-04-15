az account set --subscription onlexnet-sinnet-app-stg01
export OPERATOR1_NAME=$(az keyvault secret show --name "test-operator-1-email" --vault-name "sinnet-stg01" --query "value")
export OPERATOR1_PASSWORD=$(az keyvault secret show --name "test-operator-1-password" --vault-name "sinnet-stg01" --query "value")