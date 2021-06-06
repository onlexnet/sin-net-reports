ARG REVISION

#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
# If you are considering to use newest image, update runtime platform and local java version as well
FROM mcr.microsoft.com/java/jdk:11u11-zulu-ubuntu as build
ARG REVISION

WORKDIR /app
COPY . .

WORKDIR /app/main
RUN chmod +x ./mvnw
RUN ./mvnw clean install -Drevision=$REVISION -DskipTests -Dcheckstyle.skip -B --no-transfer-progress

#----------
# list of images is located @ https://hub.docker.com/_/microsoft-java-jre-headless
# If you are considering to use newest image, update compilation platform and local java version as well
FROM mcr.microsoft.com/java/jre-headless:11u11-zulu-alpine
ARG REVISION

# The application needs some configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_DB_HOST: db

ENV _JAVA_OPTIONS="-javaagent:/app/main/applicationinsights-agent-3.1.0.jar -Xmx512m -XX:+UseG1GC"

VOLUME /tmp
COPY --from=build /app/main/host/target/*.jar /app/
RUN mv /app/host-$REVISION.jar /app/host.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/host.jar"]