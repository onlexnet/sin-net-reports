#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
FROM mcr.microsoft.com/java/jdk:11u5-zulu-alpine as build
ARG AZURE_APP_SECRET

WORKDIR /app
COPY . .

WORKDIR /app/main
RUN chmod +x ./mvnw
# download maven dependencies and disable log entries (a lot of entries) related to downloaded artifacts
# source @ https://blogs.itemis.com/en/in-a-nutshell-removing-artifact-messages-from-maven-log-output
# we also skip tests because testing and build appllication is done in a different step of builds.
RUN ./mvnw clean install -DskipTests -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

#----------
# list of images is located @ https://hub.docker.com/_/microsoft-java-jre-headless
FROM mcr.microsoft.com/java/jre-headless:11u7-zulu-alpine

# The application needs some configuration
# so let's provide that configuration fro mAzure secrets when image is build.
ARG AZURE_APP_SECRET
ENV AZURE_APP_SECRET=$AZURE_APP_SECRET

VOLUME /tmp
COPY --from=build /app/main/host/target/*.jar /app/target/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/host-1.0-SNAPSHOT.jar"]