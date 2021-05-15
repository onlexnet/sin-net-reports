ARG REVISION

#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
FROM mcr.microsoft.com/java/jdk:11u10-zulu-alpine as build
ARG REVISION

WORKDIR /app
COPY . .

RUN ./docker-build-inside.sh
WORKDIR /app/main
RUN chmod +x ./mvnw
RUN ./mvnw clean install -Drevision=$REVISION -DskipTests -Dcheckstyle.skip -B --no-transfer-progress

#----------
# list of images is located @ https://hub.docker.com/_/microsoft-java-jre-headless
FROM mcr.microsoft.com/java/jre-headless:11u10-zulu-alpine
ARG REVISION

# The application needs some configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_DB_HOST: db

VOLUME /tmp
COPY --from=build /app/main/host/target/*.jar /app/
RUN mv /app/host-$REVISION.jar /app/host.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/host.jar"]