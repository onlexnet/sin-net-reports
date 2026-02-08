# Smoke Test Suite for SinNet Reports

This directory contains a comprehensive smoke test suite for the SinNet Reports microservices application.

## Overview

The smoke test verifies the following components:
1. **Database** - SQL Server connectivity and health
2. **Backend Services** - uservice-timeentries and uservice-webapi health checks
3. **GraphQL API** - API endpoint availability and basic queries
4. **WebApp Client** - React frontend accessibility

## Prerequisites

Before running the smoke tests, ensure you have built all components:

### 1. Build Java Services

```bash
# From repository root

# Build API client libraries
cd api/client-java
mvn clean install -DskipTests

# Build timeentries service
cd ../../uservice-timeentries
export SEMVERSION=$(cat .version)
mvn -ntp install -pl host -am -DskipTests

# Build webapi service
cd ../uservice-webapi
export SEMVERSION=$(cat .semversion)
mvn -ntp install -Drevision=$SEMVERSION -DskipTests
```

### 2. Build React Frontend

```bash
# From repository root
cd static-webapp

# Install dependencies
npm install

# Generate GraphQL models
npm run generate

# Build production bundle
npm run build
```

### 3. Verify Docker is Running

```bash
docker --version
docker compose version
```

## Running the Smoke Test

### Dockerized Approach (Recommended)

The easiest way to run smoke tests without manually invoking scripts:

```bash
cd smoke-test

# Option 1: Using Make (simplest)
make test        # Starts services, runs tests, keeps services running
make test-ci     # Runs tests and cleans up (for CI/CD)

# Option 2: Using Docker Compose directly
docker-compose --profile test up --abort-on-container-exit test-runner

# Stop services
make down        # or: docker-compose down
```

**Key benefits:**
- ✅ No manual script execution needed
- ✅ Tests run in a container (consistent environment)
- ✅ Simple `make test` or `docker-compose up` commands
- ✅ Perfect for CI/CD with exit codes

**Port conflicts?** If you get "port is already allocated" error:
```bash
cp .env.example .env
# Edit .env and change conflicting ports (e.g., DB_PORT=14330)
make test
```

See [DOCKER_COMMANDS.md](DOCKER_COMMANDS.md) for detailed Docker-based usage and [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common issues.

### Script-Based Approach (Alternative)

From the repository root:

```bash
cd smoke-test
./smoke-test.sh
```

This will:
1. Build Docker images for both microservices
2. Start all services using docker-compose
3. Wait for services to become healthy
4. Run comprehensive tests
5. Keep services running for manual testing

To run tests and shut down automatically:

```bash
./smoke-test.sh --no-wait
```

### Manual Testing

To start services without running tests:

```bash
cd smoke-test
docker-compose up -d
```

To stop services:

```bash
docker-compose down -v
```

## Test Coverage

### 1. Database Tests
- ✓ SQL Server connectivity
- ✓ Database version check
- ✓ Health check

### 2. Backend Service Tests
- ✓ Timeentries service health endpoint
- ✓ WebAPI service health endpoint
- ✓ Service startup and readiness

### 3. GraphQL API Tests
- ✓ Schema introspection
- ✓ Query type availability
- ✓ Mutation type availability
- ✓ Basic query execution (userStats, projects, customers)

### 4. WebApp Tests
- ✓ Frontend accessibility
- ✓ HTML content delivery
- ✓ Nginx proxy configuration

## Service Endpoints

When smoke test is running, services are accessible at:

| Service | URL | Description |
|---------|-----|-------------|
| WebApp | http://localhost:3000 | React frontend |
| WebAPI GraphQL | http://localhost:8081/graphql | GraphQL API endpoint |
| Timeentries Health | http://localhost:8080/actuator/health | Timeentries service health |
| WebAPI Health | http://localhost:8081/actuator/health | WebAPI service health |
| Database | localhost:1433 | SQL Server (sa/YourStrong@Passw0rd) |

## Advanced Testing

### GraphQL API Testing

Run the GraphQL API test script separately:

```bash
cd smoke-test/scripts
./test-graphql-api.sh
```

This script tests:
- GraphQL schema introspection
- Available queries and mutations
- Basic query execution
- Response validation

### Manual GraphQL Testing

You can test GraphQL queries manually using curl:

```bash
# Test schema introspection
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ __schema { types { name } } }"}'

# Test projects query
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { projects { items { entityId name } } }"}'

# Test customers query
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query { customers { items { entityId name } } }"}'
```

## Architecture

```
┌─────────────────┐
│   Static WebApp │  (Nginx + React)
│   Port: 3000    │
└────────┬────────┘
         │
         │ GraphQL
         ▼
┌─────────────────┐
│   WebAPI        │  (Spring Boot + GraphQL)
│   Port: 8081    │
└────────┬────────┘
         │
         │ gRPC
         ▼
┌─────────────────┐
│  Timeentries    │  (Spring Boot + gRPC)
│   Port: 8080    │
└────────┬────────┘
         │
         │ JDBC
         ▼
┌─────────────────┐
│   SQL Server    │  (Microsoft SQL Server)
│   Port: 1433    │
└─────────────────┘
```

## Troubleshooting

### Services Not Starting

Check service logs:
```bash
cd smoke-test
docker-compose logs -f
```

View specific service logs:
```bash
docker-compose logs -f uservice-timeentries
docker-compose logs -f uservice-webapi
docker-compose logs -f sqlserver
```

### Port Already in Use

If ports are already in use, stop conflicting services or modify `docker-compose.yml` to use different ports.

### Database Connection Issues

Verify SQL Server is healthy:
```bash
docker exec smoke-test-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "YourStrong@Passw0rd" -Q "SELECT @@VERSION"
```

### Image Build Failures

Ensure all projects are built before running smoke tests:
```bash
# Rebuild services
cd uservice-timeentries
mvn clean install -pl host -DskipTests

cd ../uservice-webapi
mvn clean install -pl host -DskipTests

cd ../static-webapp
npm install && npm run build
```

## Authentication Note

The current smoke test suite performs basic API availability tests. Full integration testing of customer creation and login functionality requires:
- Azure B2C authentication setup
- Valid user credentials
- Proper JWT token handling

For authenticated tests, refer to the main test suites in:
- `uservice-webapi/gql-test/` - GraphQL integration tests
- `uservice-timeentries/host/src/test/` - BDD tests with Cucumber

## Limitations

1. **Authentication**: Tests do not include full Azure B2C authentication flow
2. **Database State**: Each test run uses a fresh database instance
3. **Test Data**: No pre-populated test data (would need database initialization scripts)
4. **E2E Tests**: Frontend interactions are not tested (would need Selenium/Cypress)

## Future Enhancements

- [ ] Add database initialization scripts with test data
- [ ] Add Cypress E2E tests for frontend
- [ ] Add authenticated GraphQL mutation tests
- [ ] Add performance benchmarks
- [ ] Add load testing scenarios
- [ ] Integrate with CI/CD pipeline

## Contributing

When adding new smoke tests:
1. Add test scenarios to `smoke-test.sh`
2. Update this README with test coverage details
3. Ensure tests are idempotent and can run in any order
4. Add appropriate cleanup in the trap function
