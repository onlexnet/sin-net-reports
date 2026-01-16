# uservice-webapi: AI Coding Agent Guide

## Project Overview

**Spring Boot GraphQL microservice** for SinNet Reports, providing a web API layer for project management, customers, actions, and report generation. Built as part of a microservices architecture using **Dapr** for service-to-service communication via gRPC.

**Key characteristics:**
- Multi-module Maven project (Java 21 with virtual threads enabled)
- GraphQL API with Spring GraphQL (schema-first approach)
- Communicates with backend services (uservice-projects, uservice-timeentries, uservice-reports) via gRPC through Dapr
- Uses Lombok extensively with JSpecify nullness annotations
- Enforces layered architecture with ArchUnit tests
- BDD testing with Cucumber

## Architecture & Module Structure

### Maven Modules

```
uservice-webapi-build/          (parent aggregator)
├── base-pom/                   (dependency management, shared plugins)
├── build-tools/                (Checkstyle configuration)
├── gql-model/                  (GraphQL DTOs - shared between modules)
├── gql-test/                   (GraphQL test utilities and .graphql files)
└── host/                       (main application module)
```

**Always work in the `host/` module** for application code.

### Package Architecture (host module)

Enforced by `AppArchTest.java` using ArchUnit:

```
sinnet/
├── app/           (entry point: Program.java)
├── gql/           (GraphQL layer - controllers, mappers, models)
│   ├── api/       (GraphQL @Controller classes)
│   ├── service/   (business logic services)
│   └── mappers/   (gRPC ↔ GraphQL mapping)
├── ports/         (adapters to external systems)
│   └── timeentries/  (gRPC facades to backend services)
├── domain/        (domain models, entities - PROTECTED)
├── infra/         (infrastructure: time providers, etc.)
├── web/           (REST controllers for non-GraphQL endpoints)
└── ws/            (additional web services)
```

**Critical rule:** `domain` package may only be accessed by `Ports`, `App`, and `GQL` layers. Never depend on upper packages (enforced by `DependOnUpperPackagesCondition`).

## Development Workflows

### Building & Testing

```bash
# Build entire project (from uservice-webapi root)
mvnd clean install

# Skip tests during build
mvnd clean install -DskipTests

# Run specific module
cd host && mvnd clean package

# Run tests with coverage (from host/)
mvnd clean package
```

### Running Locally with Dapr

**Prerequisites:** Dapr runtime installed, backend services running

```bash
# Start with Dapr (uses task defined in .vscode/tasks.json)
# VS Code: Run Task > "dapr-debug"

# Stop Dapr
# VS Code: Run Task > "dapr-down"
```

**Configuration:** Dapr settings in `host/src/main/resources/application.properties`:
- `app.grpc.*.dapr-app-id`: Backend service app IDs
- `ENV_DAPR_GRPC_PORT`: Dapr gRPC port (default from `${DAPR_GRPC_PORT}`)

### Docker Build (via Jib)

```bash
# Build and push to local Docker (or minikube)
eval $(minikube docker-env)  # optional: use minikube images
mvnd clean install jib:dockerBuild \
  -Dimage=sinnet.azurecr.io/uservice-webapi-host:latest \
  -DskipTests
```

### Code Quality

```bash
# Run Checkstyle (automatically runs during verify phase)
mvnd verify

# Push to SonarCloud manually
mvnd clean package sonar:sonar
```

**Configuration:** Checkstyle rules in `build-tools/src/main/resources/build-checkstyle/google_checks_customized.xml`

## GraphQL Development Patterns

### Schema-First Approach

1. **Define schema:** Edit `host/src/main/resources/graphql/schema.graphqls`
2. **Create DTOs:** Add POJOs to `gql-model/src/main/java/sinnet/gql/models/` (use `@Data` from Lombok)
3. **Implement controllers:** Create `@Controller` classes in `host/src/main/java/sinnet/gql/api/`

### Controller Pattern

GraphQL controllers use `@QueryMapping` or `@MutationMapping`:

```java
@Controller
@RequiredArgsConstructor
class Query {
  @QueryMapping("Projects")
  ProjectsQuery projects() {
    var auth = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var email = auth.getPrincipal();
    return ProjectsQuery.of(email);
  }
}
```

**Nested resolvers:** Use `@SchemaMapping` on return types (e.g., `ProjectsQuery`, `CustomersQuery`) to resolve nested fields.

### Calling Backend Services (gRPC via Dapr)

Backend services are accessed through **facade interfaces** in `sinnet.ports.timeentries`:

1. **Inject facade:** `@RequiredArgsConstructor` + `private final ProjectsGrpcFacade projectsFacade;`
2. **Call methods:** Facades abstract gRPC stubs and Dapr communication
3. **Map results:** Use mappers in `sinnet.gql.api` (e.g., `CustomerMapper`) to convert gRPC models to GraphQL DTOs

**Example facade usage:**
```java
var projectId = projectsFacade.create(userEmail);
var entities = customersFacade.list(userToken);
```

**Configuration:** Each backend service has a `*GrpcConfig` class (e.g., `ProjectsGrpcConfig`) setting up the gRPC stub with Dapr.

## Java Code Conventions

**Follow instructions in `.github/instructions/java.instructions.md`:**

- **Records** for DTOs (prefer over classes)
- **Pattern matching** for `instanceof` and `switch`
- **Immutability:** Use `final` fields, `List.of()`, `Stream.toList()`
- **Lombok:** Use `@Data` (DTOs), `@RequiredArgsConstructor` (services), `@Builder` sparingly
- **Nullness:** Use `@NonNull`/`@Nullable` from JSpecify (configured in `lombok.config`)
- **No field injection:** Constructor injection only (enforced by ArchUnit)

### Common Violations to Avoid

- ❌ `System.out.println()` (use `@Slf4j` + `log.info()`)
- ❌ Generic exceptions (`throws Exception`)
- ❌ Empty catch blocks
- ❌ Magic numbers (use constants)
- ❌ `==` for object comparison (use `.equals()`)

## Testing Patterns

### BDD Tests (Cucumber)

Located in `host/src/test/java/sinnet/bdd/` with features in `host/src/test/resources/bdd/`:

```java
@Given("user has project {string}")
public void userHasProject(String projectName) { ... }
```

**Run tests:** `mvnd test` or via IDE (JUnit 5)

### GraphQL Integration Tests

Use `gql-test` module utilities:
- Load `.graphql` queries from `gql-test/src/main/resources/graphql-test/`
- Example: `namedProjects.graphql`, `newAction.graphql`

### Architecture Tests

`AppArchTest.java` validates:
- Package dependencies (no upper package access)
- Layer separation (domain isolation)
- General coding rules (no field injection, etc.)

**Always run after structural changes:**
```bash
mvnd test -Dtest=AppArchTest
```

## Key Files Reference

- **Entry point:** [host/src/main/java/sinnet/app/Program.java](host/src/main/java/sinnet/app/Program.java)
- **GraphQL schema:** [host/src/main/resources/graphql/schema.graphqls](host/src/main/resources/graphql/schema.graphqls)
- **Application config:** [host/src/main/resources/application.properties](host/src/main/resources/application.properties)
- **Architecture tests:** [host/src/test/java/sinnet/AppArchTest.java](host/src/test/java/sinnet/AppArchTest.java)
- **Lombok config:** [lombok.config](lombok.config)
- **Build config:** [base-pom/pom.xml](base-pom/pom.xml) (dependencies), [pom.xml](pom.xml) (modules)

## Debugging & Development

- **GraphiQL UI:** http://localhost:11031/graphiql (when running locally)
- **Health check:** http://localhost:11031/actuator/health
- **Virtual threads:** Enabled by default (`spring.threads.virtual.enabled=true`)
- **Profiles:** `dev`, `test`, `prod` in `application-{profile}.properties`

## Common Tasks Quick Reference

| Task | Command |
|------|---------|
| Full build | `mvnd clean install` |
| Build without tests | `mvnd clean install -DskipTests` |
| Run specific test | `mvnd test -Dtest=ClassName` |
| Check code style | `mvnd verify` |
| Generate Docker image | `mvnd jib:dockerBuild -Dimage=<name>` |
| Start with Dapr | VS Code Task: "dapr-debug" |
