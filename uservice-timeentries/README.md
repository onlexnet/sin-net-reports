# Dev notes

## Profiles
We support some profiles in backend code to allow run them with different configurations:
- dev - no security
- prod - ready for deployment, mssql database

## build project

### Simple one-command build (recommended)
```bash
./build.sh
```
This script automatically builds all required dependencies (api/client-java and libs-java) before building this service. Tests are skipped by default as they require external services.

To run with tests:
```bash
./build.sh -Dtest=SomeSpecificTest  # Run specific test
# or pass any other Maven arguments
```

### Manual build (legacy)
If you prefer to build dependencies manually:
```bash
mvn -f ../api/client-java install -ntp
mvn -f ../libs-java install -ntp
mvn clean install -ntp -DskipTests
```

## Good development practices
- **code ../.github/workflows/uservice-timeentries.yml** to see build definition
- **[mvn versions:display-property-updates -ntp](https://www.mojohaus.org/versions/versions-maven-plugin/display-property-updates-mojo.html)** Displays properties that are linked to artifact versions and have updates available
- **[mvn versions:display-plugin-updates](https://www.mojohaus.org/versions-maven-plugin/display-plugin-updates-mojo.html)** to display all plugins that have newer versions available, taking care of Maven version prerequisites
- **[mvn project-info-reports:licenses]

## Used articles
- https://www.vinsguru.com/grpc-client-streaming-api-in-java/
- https://github.com/grpc/grpc-java/blob/master/README.md
- Integrate [Micrometer](https://developer.ibm.com/technologies/java/tutorials/monitor-spring-boot-microservices/)
- [How to design a good JWT authentication filter](https://stackoverflow.com/questions/41975045/how-to-design-a-good-jwt-authentication-filter)
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

## Coding principles

Docker:
```bash
  # example: to build images to locally deploy to k8s
  # 1) Optionally change context to work on minikube images
  eval $(minikube docker-env)
  # 2) to build image and push it to local k8s instance
  mvnd jib:dockerBuild -pl host clean install -DskipTests
  mvnd jib:dockerBuild -pl host -Dimage=sinnet.azurecr.io/uservice-timeentries-host:latest
  mvnd jib:dockerBuild -pl initdb-host clean install -DskipTests
  mvnd jib:dockerBuild -pl initdb-host -Dimage=sinnet.azurecr.io/uservice-timeentries-initdb

  # example: build local tar file with image
  mvn clean compile jib:buildTar -pl host
```

