### Prerequisites
* Application named **onlex-infra** *(single tenant application)* with 'Contributor' role (to create resources) and 'Application administrator' to create service principals required to run code and use resources for separated applications and environments.
  * with secret named e.g. 'terraform-cli' (used by support from CLI tools)
  * with secret named e.g. 'terraform-cicd' (used in CICD pipeline)
* Container to keep terraform state ***az storage container create -n tfstate --account-name \<YourAzureStorageAccountName> --account-key \<YourAzureStorageAccountKey>***

### Environment variables
set properly variables (for CI in pipeline, for CLI in local environment):

# To simplify management of multiple application in very smalll organization OnLex.net
# We use one infra account (with Contributor role) to allow access to Azure infrastructure
export ARM_CLIENT_ID="onlex-infra" # required by azurerm and azuread providers
export ARM_CLIENT_CERTIFICATE_PASSWORD= ... proper value from secrets mentioned above # required by azurerm provider
export ARM_CLIENT_SECRET= ... proper value from secrets mentioned above # required by azuread provider
export ARM_SUBSCRIPTION_ID= ... onlex prod subscription # required by azurerm provider
export ARM_TENANT_ID= ... onlex.net tenant # required by azurerm and azuread providers

### Work locally
* Assumption: use bash
* add env variables as described above
* init your terraform
    **terraform init**
* apply changes on production manually
    **terraform apply -var-file prd.tfvars**

**terraform plan** to see plan of changes for your current desired infrastructure