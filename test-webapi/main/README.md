# BDD tests for webapi

## Prerequisits
- In the tests we use some predefined users, already existing in Azure B2C, named:
  - Operator1 - person using the system to write its own timeentries
  - Operator2 - person using the system to write its own timeentries
  - ProjectOwner1 - designed to be used to create its own project
  - ProjectOwner2 - designed to be used to create its own project
  - Hint: all users (and their passwords) are created manually as today we can't create them using Terraform with Azure B2C
- passwords for all users are defined and kept in Azure Vault and available for the test as it is run in context of test service principal

## Start work locally
``` bash
sudo service docker start # to run docker locally - required just if docker is not yet started
```

``` bash
docker-compose up # to run services
./start-stack.sh # to run application services
```

and, in new shell
``` bash
. init-vars.sh # to create env variables based on KeyVault secrets
mvn clean test # to run tests
```

## useful links
- [https://github.com/cucumber/cucumber-jvm](https://github.com/cucumber/cucumber-jvm)
- [GraphQL client](https://hantsy.github.io/blog/2021/consuming-graphql-apis-with-quarkus/)
