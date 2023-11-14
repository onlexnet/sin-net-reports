#!/bin/sh

# env.sh

# Change the contents of this output to get the environment variables of interest. 
# The variablees should be already defined in Terraform Cloud where the script is executed
# The output must be valid JSON, with strings for both keys and values.
# WARNIK: such variables are not used, but I keep such example for future scenarios
cat <<EOF
{
  "TF_VAR_NORDIGEN_SECRET_ID": "$TF_VAR_NORDIGEN_SECRET_ID",
  "TF_VAR_NORDIGEN_SECRET_KEY": "$TF_VAR_NORDIGEN_SECRET_KEY",
  "CR_PAT": "$CR_PAT",
  "ONLEXNET_TENANT_ID": "$ARM_TENANT_ID",
  "ONLEXNET_SINNET_PRD01_SUBSCRIPTION_ID": "$TF_VAR_azure_subscription_id_dev",
  "ONLEXNET_INFRA_CLIENT_ID": "$ARM_CLIENT_ID",
  "ONLEXNET_INFRA_SECRET": "$ARM_CLIENT_SECRET",
  "SINNETAPP_PROD_SECRET": "$SINNETAPP_PROD_SECRET"
}
EOF
