# SinNet Reports - AI Coding Agent Instructions

Time tracking and reporting microservices application: Java Spring Boot backends + React frontend + Dapr + Azure Container Apps.

## Quick Start - Local Development

**Single command to run everything:**
```bash
docker compose up --build
```

Access points:
- Frontend: http://localhost:3000
- GraphQL API: http://localhost:11031/graphiql  
- TimeEntries health: http://localhost:11021/actuator/health
- SQL Server: localhost:1433 (sa/P@ssw0rd123!)

See [LOCAL_STACK.md](LOCAL_STACK.md) for detailed local development guide.

## Architecture & Communication Patterns

### Service Communication via Dapr
Services communicate through **Dapr sidecars**, not direct gRPC:
- `uservice-timeentries` (port 11021) ← Dapr sidecar → `uservice-webapi` (port 11031)
- gRPC schemas in `api/schema/uservice-timeentries.rpc/*.proto`
- Client code generated in `api/client-java` (build this first!)

**In production (Azure Container Apps):**
- Init containers download ApplicationInsights agent to ephemeral volume
- Agent loaded at runtime via `JAVA_TOOL_OPTIONS`, NOT bundled in Docker images
- See `infra/shared/module_container_app_*/main.tf` for init container patterns

### Version Management Pattern
**Three separate version files** with different formats:
- `uservice-timeentries/.version` → `SEMVERSION` env var (e.g., "1.0.9")
- `uservice-webapi/.semversion` → Maven `-Drevision` property (e.g., "0.1.3")
- `static-webapp/.version` → npm version (e.g., "1.4.3")

Always use `$(cat .version)` pattern, never hardcode versions.

## Critical Build Dependencies

### GraphQL Schema → Frontend Code Generation
React app **requires generated types** before build:
```bash
cd static-webapp
npm run generate  # Reads ../uservice-webapi/**/*.graphqls, outputs to src/components/.generated/
npm run build
```

**Dual schema paths** in `graphql-codegen-config.yml`:
- `../uservice-webapi/**/*.graphqls` - local dev (parent directory context)
- `uservice-webapi/**/*.graphqls` - Docker build (schema copied to build context)

### Maven Module Build Order
Always build in this order (dependencies matter):
```bash
mvn install -f api/client-java              # Generates gRPC clients from .proto
mvn install -pl host -am -f uservice-timeentries  # Uses api/client-java
mvn install -f uservice-webapi              # Uses api/client-java
```

Use `-DskipTests` - Testcontainers tests often fail in containerized dev environments.

## Spring Profiles & Database

**Dev profile** (`SPRING_PROFILES_ACTIVE=dev`):
- Uses SQL Server with `tempdb` database
- Relaxed security, auto-schema creation
- See `docker-compose.yml` environment vars

**Prod profile** (Azure):
- Full authentication via Azure B2C
- SQL Server with proper database/schema
- ApplicationInsights monitoring enabled

## Docker Build Patterns

### Multi-stage Dockerfiles
All services use multi-stage builds with Maven cache optimization:
```dockerfile
# Stage 1: Build with Maven cache mount
RUN --mount=type=cache,target=/root/.m2 \
    mvn install -DskipTests

# Stage 2: Runtime with JRE only (not JDK)
FROM eclipse-temurin:25-jre-jammy
```

### Docker Compose Build Context
All Dockerfiles use **root context** (`.`) because they need:
- `api/client-java` - shared gRPC libraries
- `api/schema` - proto definitions
- Service source code

Example: `docker compose up` builds from root, copies dependencies correctly.

## Deployment & Infrastructure

**Target platform:** Azure Container Apps (via Terraform in `infra/`)
- **NOT Kubernetes** - uses Azure Container Apps native features
- Deployment triggered by push to `main` branch → PRD01 environment
- GitHub Actions workflows in `.github/workflows/`

**Key Terraform modules:**
- `module_container_app_timeentries` - init container downloads AppInsights agent
- `module_container_app_webapi` - same pattern
- `module_static_webapp` - Azure Static Web Apps

## Critical Non-Obvious Patterns

1. **Never bundle ApplicationInsights agent in images** - use init containers in Azure, not in Dockerfile or CI builds
2. **GraphQL codegen before npm build** - frontend won't compile without generated types
3. **Dapr service invocation** - services never call each other directly, always via Dapr sidecar
4. **Three version systems** - timeentries uses `.version`, webapi uses `.semversion`, frontend uses `.version`
5. **Maven revision property** - webapi requires `-Drevision=$SEMVERSION`, not standard versioning
6. **Dual-path schemas** - graphql-codegen-config.yml needs both `../` and non-`../` paths for local + Docker

## File References

Key files for understanding architecture:
- `docker-compose.yml` - complete local stack with 8 services
- `LOCAL_STACK.md` - comprehensive local development guide
- `infra/shared/main.tf` - Azure infrastructure modules
- `api/schema/` - gRPC service definitions
- `uservice-webapi/host/src/main/resources/graphql/schema.graphqls` - GraphQL schema consumed by frontend