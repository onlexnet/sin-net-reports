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
  "CR_PAT": "$CR_PAT"
}
EOF
