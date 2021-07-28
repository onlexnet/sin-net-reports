# Goal
Infrastructre as  code: creates environments and configure them

### Prerequisites
* We need a privileged service account to apply changes in Azure. For such reason application named **onlex-infra** *(single tenant application) has been created with some permissions:
  - 'Contributor' role (to be able create resources) 
  - 'Application administrator' to create service principals used in environments
  - with a secret named e.g. 'terraform-cli' (used to support CLI tool)
* We need a storage to keep terraform configuration. For such purpose there is created storage ***az storage container create -n tfstate --account-name \<YourAzureStorageAccountName> --account-key \<YourAzureStorageAccountKey>***
* We need a superuser role in external PostgreSQL server to create databases and schemas. The name of the role os **onlex_infra**, and password for such role is provided as environment variable (described below). **(The account has been created using *CREATE ROLE onlex_infra LOGIN SUPERUSER PASSWORD 'some password';*)**

### Environment variables
set properly variables (for CI in pipeline, for CLI in local environment):

# To simplify management of multiple application in very small organization OnLex.net
# We use one infra account (with Contributor role) to allow access to Azure infrastructure
export ARM_CLIENT_ID="onlex-infra" # required by azurerm and azuread providers. Defines a principal able to create resources
export ARM_CLIENT_CERTIFICATE_PASSWORD= ... proper value from secrets mentioned above # required by azurerm provider
export ARM_CLIENT_SECRET= ... proper value from secrets mentioned above # required by azuread provider
export ARM_TENANT_ID= ... onlex.net tenant # Tenant where the resources are created. Required by azurerm and azuread providers
export ARM_SUBSCRIPTION_ID= ... onlex prod subscription # required by backend provider

### Work locally
* Assumption: use bash
* add env variables as described above
* init your terraform
    **terraform init**
* get access to remote secured database
  because database is (by design) secured on remote VM
  we assume port 5432 is already redirected from database VM
  ssh -L 5432:localhost:5432 <USERNAME>@raport.sin.net.pl
    
* apply changes on production manually
    **terraform apply -var-file prd.tfvars**
    where subscription_id will be used as subscription for created resources (especially resource group)

**terraform plan** to see plan of changes for your current desired infrastructure

### Not used but promising articles
- https://medium.com/citihub/a-more-secure-way-to-call-kubectl-from-terraform-1052adf37af8
- https://docs.microsoft.com/en-us/azure/key-vault/general/key-vault-integrate-kubernetes
- https://mrdevops.io/introducing-azure-key-vault-to-kubernetes-931f82364354