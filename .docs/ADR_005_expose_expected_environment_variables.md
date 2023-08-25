# Context: TO run application we need to know what configuration  elements are expected

## Decision
application.properties contains all know app variables, named with prefix ENV_

## Reason
- We need to know variables required by application to run it in many cases: integration test, local developer configuration, target environment

## Consequences: 
- if you have some additional, profil-related variables, they should be located outside main application.properties making them less visible

## Notes:
- None
