# Dev hints

## Build project

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

## Other tasks

Sonar:  
- https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/sonarscanner-for-maven/
```
  # push manually sonar result with jacoco coverage:
  mvnd clean package sonar:sonar
```

Docker:  
```bash
  # example
  # 1) Optionally change context to work on minikube images
  eval $(minikube docker-env)
  # 2) to build image and push it to local k8s instance
  mvnd clean install jib:dockerBuild -Dimage=sinnet.azurecr.io/uservice-webapi-host:latest -DskipTests

```


# Interesting articles:
- https://medium.com/swlh/part-ii-spring-boot-app-development-with-skaffold-kustomize-and-jib-aa663e133558
