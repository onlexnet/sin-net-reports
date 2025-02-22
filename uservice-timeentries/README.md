# Dev notes

## Profiles
We support some profiles in backend code to allow run them with different configurations:
- dev - no security
- prod - ready for deployment, mssql database

## build project
```bash
mvn -f ../api/client-java install -ntp
mvn -f ../libs-java install -ntp
mvn clean install -ntp
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

