Context: Run local stack of µservices and services locally without touching external environments

Decision: Docker compose and run from sources

Solutions:
- Kubernetes: Deploy to local kubernetes and test locally
- Dev env: Deploy to external dev environment and tests whole solution
- Docker compose and run from sources: Run al lservices as Docker image,, and run the rest from sources

Reason: It seems to allow the much control over how to run locally, even more making possible  ModelMapper is widely used, it has integration with [Vavr](https://github.com/vavr-io/vavr-jackson) models and [Proto](https://github.com/HubSpot/jackson-datatype-protobuf)

Consequences: Java, as it is not a script lang, needs some tricks to be able to run from sources. Solution is not a universal, see notes

Notes:
- For Spring, to run with *spring-boot:run* main module and dependencies see https://github.com/spring-projects/spring-boot/issues/3436#issuecomment-236213126
