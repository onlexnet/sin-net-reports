# Goal
Infrastructre as code: creates environments and configure them

### Prerequisites
* We need a privileged service account to apply changes in Azure. For such reason application named **onlex-infra** (single tenant application) has been created with some permissions:
  - 'Contributor' role (to be able create resources) 
  - 'Application administrator' to create service principals used in environments
  - with a secret named e.g. 'terraform-cli' (used to support CLI tool)
* We need a storage to keep terraform backend configuration. For such purpose there is created storage ***az storage container create -n tfstate --account-name \<YourAzureStorageAccountName> --account-key \<YourAzureStorageAccountKey>***
* We need a superuser role in external PostgreSQL server to create databases and schemas. The name of the role os **onlex_infra**, and password for such role is provided as environment variable (described below). **(The account has been created using *CREATE ROLE onlex_infra LOGIN SUPERUSER PASSWORD 'some password';*)**
If you do not remember the password change it:
* login to host machine using ssh connection
* open SQL console *sudo psql -U postgres*
* change password *ALTER USER onlex_infra WITH PASSWORD 'new exciting password'* 

### Set prerequisit environment variables for local environment
set properly variables (for CI in pipeline, for CLI in local environment).
We suggest to create local - never commited - bash file in user home directory, where all required environment variables are defined. The file may be named 'onlex-sinnet-init.sh' with values:
```bash
export ARM_CLIENT_ID= ... client ID of onlex-infra # required by azurerm and azuread providers. Defines a principal able to create resources
export ARM_CLIENT_CERTIFICATE_PASSWORD= ... proper value from secrets mentioned above # required by azurerm provider
export ARM_CLIENT_SECRET= ... proper value from secrets mentioned above # required by azuread provider
export ARM_TENANT_ID= ... onlex.net tenant # Tenant where the resources are created. Required by azurerm and azuread providers
export ARM_SUBSCRIPTION_ID= ... OnLexNet subscription ID # required by backend provider
export TF_VAR_sinnet_k8s_host=raport.sin.net.pl
export TF_VAR_sinnet_k8s_token= ... generate using token=$(microk8s kubectl -n kube-system get secret | grep default-token | cut -d " " -f1)
                                                   microk8s kubectl -n kube-system describe secret $token
export TF_VAR_subscription_id= ... onlexnet-sinnet-prod subscription ID
```
, so is enough to run *. ~/onlex-sinnet-init.sh* to have fully working terraform.


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

**terraform plan** to see plan of changes for your current desired infrastructure

### Not used but promising articles
- https://medium.com/citihub/a-more-secure-way-to-call-kubectl-from-terraform-1052adf37af8
- https://docs.microsoft.com/en-us/azure/key-vault/general/key-vault-integrate-kubernetes
- https://mrdevops.io/introducing-azure-key-vault-to-kubernetes-931f82364354