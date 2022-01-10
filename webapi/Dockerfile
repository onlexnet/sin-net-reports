ARG REVISION

#----------
# more images see https://hub.docker.com/_/microsoft-java-jdk
# If you are considering to use newest image, update runtime platform and local java version as well
FROM mcr.microsoft.com/java/jdk:11u13-zulu-ubuntu as build
ARG REVISION

WORKDIR /app
COPY . .

WORKDIR /app/main
RUN chmod +x ./mvnw
RUN ./mvnw clean install -Drevision=$REVISION -DskipTests -Dcheckstyle.skip -B --no-transfer-progress

#----------
# list of images is located @ https://hub.docker.com/_/microsoft-java-jre-headless
# If you are considering to use newest image, update compilation platform and local java version as well
FROM mcr.microsoft.com/java/jre-headless:11u13-zulu-alpine
ARG REVISION

# The application needs some configuration
ENV SPRING_PROFILES_ACTIVE=prod

# the application os fat jar so only one file is required to run the app
COPY --from=build /app/main/host/target/*.jar /app/

# we we require application insight agent when app is run
# let's copy agent from expected location and set properly jvm option
COPY --from=build /app/applicationinsights /app/
ENV _JAVA_OPTIONS="-javaagent:/app/applicationinsights-agent-3.1.0.jar -Xmx512m -XX:+UseG1GC"

# fat-jar name is well-known, let's simplify the name so that we can use simpler name in ENTRYPOINT
RUN mv /app/host-$REVISION.jar /app/host.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/host.jar"]