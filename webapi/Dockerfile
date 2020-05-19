#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
FROM mcr.microsoft.com/java/jdk:11u5-zulu-alpine as build

WORKDIR /app
COPY . .

WORKDIR /app/main
RUN chmod +x ./mvnw
# download maven dependencies and disable log entries (a lot of entries) related to downloaded artifacts
# source @ https://blogs.itemis.com/en/in-a-nutshell-removing-artifact-messages-from-maven-log-output
RUN ./mvnw clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

#----------
FROM mcr.microsoft.com/java/jre-headless:11u6-zulu-alpine

# The pplication needs some configuration
ARG AZURE_APP_SECRET
ENV AZURE_APP_SECRET=$AZURE_APP_SECRET

VOLUME /tmp
COPY --from=build /app/main/host/target/*.jar /app/target/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/host-1.0-SNAPSHOT.jar"]