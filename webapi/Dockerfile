ARG REVISION

#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
FROM mcr.microsoft.com/java/jdk:11u5-zulu-alpine as build

WORKDIR /app
COPY . .

WORKDIR /app/main
RUN chmod +x ./mvnw
# download maven dependencies and disable log entries (a lot of entries) related to downloaded artifacts
# source @ https://blogs.itemis.com/en/in-a-nutshell-removing-artifact-messages-from-maven-log-output
# we also skip tests because testing and build appllication is done in a different step of builds.
ENV REVISION=$REVISION
RUN ./mvnw clean install -Drevision=$REVISION -DskipTests -B --no-transfer-progress

#----------
# list of images is located @ https://hub.docker.com/_/microsoft-java-jre-headless
FROM mcr.microsoft.com/java/jre-headless:11u7-zulu-alpine

# The application needs some configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_DB_HOST: db

VOLUME /tmp
COPY --from=build /app/main/host/target/*.jar /app/target/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/host-1.0-SNAPSHOT.jar"]