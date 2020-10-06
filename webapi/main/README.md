# Dev notes

## Profiles
We support some profiles in backend code to allow run them with different configurations:
- dev - no security, inmemory database
- prod - ready for deployment, postgresql database

## Database migration
- create new entity/entities
- see all options using **mvn -pl domain-core liquibase:help**
- generate new changeset **mvn -pl domain-core liquibase:diff**

## Good development practices

- **[mvn versions:display-dependency-updates](https://www.mojohaus.org/versions-maven-plugin/display-dependency-updates-mojo.html)** to display all dependencies that have newer versions available
- **[mvn versions:display-plugin-updates](https://www.mojohaus.org/versions-maven-plugin/display-plugin-updates-mojo.html)** to display all plugins that have newer versions available, taking care of Maven version prerequisites
- **[mvn project-info-reports:licenses]

<https://www.freecodecamp.org/news/cjn-junit-test-with-maven-in-vscode/>
https://github.com/SonarSource/sonar-scanning-examples/blob/master/doc/jacoco.md


## Used articles
- Integrate [Micrometer](https://developer.ibm.com/technologies/java/tutorials/monitor-spring-boot-microservices/)
- use [R2DBC in Spring](https://www.2ndquadrant.com/en/blog/building-reactive-postgresql-repositories-for-spring-boot-applications-part-1/)
- [How to design a good JWT authentication filter](https://stackoverflow.com/questions/41975045/how-to-design-a-good-jwt-authentication-filter)