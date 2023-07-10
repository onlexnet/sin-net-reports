# Goal
Infrastructre as code: creates environments and configure them
State location: Terraform Cloud

# Manual preparation
- Create account at hashicorp.com as we keep state in the HC cloud

# Start locally
```bash
terraform login # to connect to terraform cloud
```

### Prerequisites
The infrastructure is currently designed to be started by manual invocation by a person with proper permissions. It is not designed to be isued in CICD (what may be the next step), even more: AFAIK some of operations can;t be automated today (e.g. creation of B2C), so it is the next reason why I would liek to simplify my startup by just describing terraform resources but invoking them manualy according to my time and needs.

### Work locally
* Assumption: use bash
* go to folder where you would like to apply changes (e.g. cd main/dev01/)
* apply changes on selected env manually
  ```bash
  cd main/dev01
  terraform init # init your terraform once 
  terraform apply
  ```

### Refresh providers
```bash
rm -fr .terraform
terraform init -upgrade
```
