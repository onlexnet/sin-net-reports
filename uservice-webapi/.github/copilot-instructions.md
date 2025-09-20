# SinNet Reports - uservice-webapi

## Architecture Overview

This is a Spring Boot application that provides a GraphQL API as part of a microservices architecture. The application uses:

- **Spring Boot** for application framework with Java 21 virtual threads
- **GraphQL** for API queries and mutations
- **Dapr** for service-to-service communication via gRPC
- **Maven** for build management (with multi-module structure)
- **Azure Application Insights** for monitoring

The codebase follows a modular architecture with these key modules:
- `host`: Main application module with Spring Boot app
- `gql-model`: GraphQL model definitions
- `gql-test`: GraphQL testing utilities
- `base-pom`: Common Maven configuration

## Key Components and Patterns

1. **GraphQL Controllers**: Controllers are annotated with `@Controller` and defined in `sinnet.gql.api` package, with separate classes for queries and mutations.

2. **Service Communication**: Services communicate using gRPC through Dapr sidecar (see `sinnet.grpc` package). Each service has its own configuration class (e.g., `ProjectsGrpcConfig`).

3. **Architecture Rules**: The codebase enforces architecture rules using ArchUnit (see `AppArchTest.java`):
   - No dependencies on upper packages
   - No field injection (constructor injection preferred)
   - No generic exceptions
   - No direct use of System.out or System.err

4. **Testing**: The project uses:
   - JUnit 5 for unit tests
   - Cucumber for BDD tests
   - Spring Boot test framework

## Development Workflow

### Building the Application

```bash
# Full build with tests
mvn clean package

# Build with Sonar analysis (includes JaCoCo coverage)
mvn clean package sonar:sonar

# Build Docker image
mvn clean install jib:dockerBuild -Dimage=sinnet.azurecr.io/uservice-webapi-host:latest -DskipTests
```

### Running the Application

The application requires Dapr sidecar to be running. Use the dapr-debug task to run with Dapr:

```bash
# Start with Dapr sidecar
dapr run --app-id projects --app-port 11031 --dapr-grpc-port 11039 --components-path ../../.components -- java -jar target/uservice-webapi-host-0.1.3.jar
```

### Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ApplicationContextTest
```

## Key Files and Integration Points

- `host/src/main/resources/application.properties`: Main configuration file
- `host/src/main/resources/graphql/schema.graphqls`: GraphQL schema definition
- `host/src/main/java/sinnet/gql/api/`: GraphQL controllers
- `host/src/main/java/sinnet/grpc/`: gRPC client configurations for service communication

## Conventions and Patterns

1. **GraphQL Pattern**: Each GraphQL operation is implemented in its own controller class following a naming convention of `[EntityName][QueryOrMutation][Operation]` (e.g., `ActionsQuerySearch.java`).

2. **Error Handling**: The application uses Vavr for functional error handling patterns.

3. **Configuration**: The application uses Spring Boot properties for configuration with environment-specific profiles (dev, test, prod).

4. **Security**: Authentication is configured to use OAuth (Azure B2C), but is currently commented out in production.
