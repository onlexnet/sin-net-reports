# Dev hints

Sonar:  
- https://docs.sonarcloud.io/advanced-setup/ci-based-analysis/sonarscanner-for-maven/
```
  # push manually sonar result with jacoco coverage:
  mvnd clean package sonar:sonar
```

Docker:  
```
  # example: to build image for tests
  mvn clean compile jib:dockerBuild -pl host -Dimage=my-image-name

  # example: build local tar file with image
  mvn clean compile jib:buildTar -pl host
```

# Interesting articles:
- https://medium.com/swlh/part-ii-spring-boot-app-development-with-skaffold-kustomize-and-jib-aa663e133558
