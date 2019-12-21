#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
FROM mcr.microsoft.com/java/jdk:11u5-zulu-alpine as install-packages

WORKDIR /app
COPY . .

RUN chmod +x ./mvnw
# download maven dependencies and disable log entries (a lot of entries) related to downloaded artifacts
# source @ https://blogs.itemis.com/en/in-a-nutshell-removing-artifact-messages-from-maven-log-output
RUN ./mvnw clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn


#----------
FROM install-packages as test-runner
# run tests to see if they are working
# run verify because aggregation of code coverage is done in verify phase by report-aggregate module
# additionally in verify phase checkstyle audit is applied.
RUN ./mvnw test verify

#----------
FROM test-runner as build
WORKDIR /app

COPY . .

RUN ./mvnw install -DskipTests


#----------
FROM mcr.microsoft.com/java/jre-headless:11u5-zulu-alpine
VOLUME /tmp
COPY --from=build /app/webapi/target/*.jar /app/target/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/webapi-0.0.1-SNAPSHOT.jar"]