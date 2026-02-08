# Multi-stage build for uservice-timeentries
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

# Install Maven 3.9+ (required by api/client-java maven-enforcer-plugin)
RUN apt-get update && \
    apt-get install -y wget && \
    wget --no-verbose https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz && \
    tar xzf apache-maven-3.9.9-bin.tar.gz -C /opt/ && \
    ln -s /opt/apache-maven-3.9.9 /opt/maven && \
    rm apache-maven-3.9.9-bin.tar.gz && \
    rm -rf /var/lib/apt/lists/*

ENV MAVEN_HOME=/opt/maven
ENV PATH=$PATH:$MAVEN_HOME/bin

# Copy api/client-java dependency first (needed by timeentries)
COPY api/client-java api/client-java
COPY api/schema api/schema

# Copy Maven wrapper and pom files for timeentries
COPY uservice-timeentries/mvnw uservice-timeentries/
COPY uservice-timeentries/.mvn uservice-timeentries/.mvn
COPY uservice-timeentries/pom.xml uservice-timeentries/
COPY uservice-timeentries/.version uservice-timeentries/
COPY uservice-timeentries/base-pom/pom.xml uservice-timeentries/base-pom/
COPY uservice-timeentries/build-tools/pom.xml uservice-timeentries/build-tools/
COPY uservice-timeentries/host/pom.xml uservice-timeentries/host/
COPY uservice-timeentries/initdb-host/pom.xml uservice-timeentries/initdb-host/
COPY uservice-timeentries/initdb-migrate/pom.xml uservice-timeentries/initdb-migrate/
COPY uservice-timeentries/initdb-test/pom.xml uservice-timeentries/initdb-test/
COPY uservice-timeentries/report-aggregate/pom.xml uservice-timeentries/report-aggregate/
COPY uservice-timeentries/lombok.config uservice-timeentries/

# Make Maven wrapper executable
RUN chmod +x uservice-timeentries/mvnw

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    cd uservice-timeentries && \
    mvn dependency:go-offline -B -ntp -DskipTests -Djava.version=21 || true

# Copy source code (only modules with src directories - base-pom has no src, components is empty)
COPY uservice-timeentries/build-tools/src uservice-timeentries/build-tools/src
COPY uservice-timeentries/host/src uservice-timeentries/host/src
COPY uservice-timeentries/initdb-host/src uservice-timeentries/initdb-host/src
COPY uservice-timeentries/initdb-migrate/src uservice-timeentries/initdb-migrate/src
COPY uservice-timeentries/initdb-test/src uservice-timeentries/initdb-test/src
COPY uservice-timeentries/report-aggregate/src uservice-timeentries/report-aggregate/src

# Build api/client-java with Java 21 override
RUN --mount=type=cache,target=/root/.m2 \
    cd api/client-java && \
    mvn clean install -B -ntp -DskipTests -Djava.version=21 || true

# Build the application
RUN --mount=type=cache,target=/root/.m2 \
    cd uservice-timeentries && \
    export SEMVERSION=$(cat .version) && \
    mvn clean install -pl host -am -DskipTests -B -ntp -Djava.version=21

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from builder stage
COPY --from=builder /workspace/uservice-timeentries/host/target/*.jar app.jar

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose ports
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]
