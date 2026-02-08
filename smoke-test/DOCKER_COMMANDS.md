# Dockerized Smoke Test Commands

This document describes the fully dockerized approach to running smoke tests without manually invoking scripts.

## Quick Start

### Option 1: Using Make (Recommended)

```bash
# Show all available commands
make help

# Start services and run tests
make test

# Run tests in CI mode (exits after completion)
make test-ci

# Just start services (no tests)
make up

# Stop services
make down

# View logs
make logs
```

### Option 2: Using Docker Compose Directly

```bash
# Start all services (no tests)
docker-compose up -d

# Start services and run tests
docker-compose --profile test up --abort-on-container-exit test-runner

# Run tests in CI mode (exits and cleans up)
docker-compose --profile test up --abort-on-container-exit --exit-code-from test-runner test-runner
docker-compose down -v

# Stop all services
docker-compose --profile test down

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

## Service Profiles

The docker-compose configuration uses profiles to control when tests run:

- **Default profile**: Only starts the application services (database, backend, frontend)
- **test profile**: Includes the test-runner container that automatically runs smoke tests

## Test Runner Container

The `test-runner` service is a lightweight Alpine Linux container that:
- Waits for all services to become healthy
- Runs comprehensive smoke tests against all services
- Reports results with colored output
- Exits with code 0 on success, non-zero on failure

## Advantages of This Approach

1. **No manual script execution**: Just use `docker-compose up`
2. **Consistent environment**: Tests run in a container, not on host
3. **CI/CD friendly**: Exit codes and --abort-on-container-exit work perfectly
4. **Easy to extend**: Add more test containers as needed
5. **Clean separation**: Services vs tests are clearly separated by profiles

## Examples

### Development Workflow

```bash
# Start services for development
cd smoke-test
make up

# Access services:
# - WebApp: http://localhost:3000
# - GraphQL: http://localhost:8081/graphql
# - Timeentries: http://localhost:8080/actuator/health

# Stop when done
make down
```

### Running Tests

```bash
# Run full smoke test suite
cd smoke-test
make test

# Services remain running after tests
# Stop with: make down
```

### CI Pipeline

```bash
# In your CI pipeline
cd smoke-test
make test-ci  # Runs tests and cleans up automatically
```

## Building Images

Before running smoke tests, you need to build the Docker images:

```bash
cd smoke-test
make build
```

Or build manually:

```bash
# Build timeentries service image
cd uservice-timeentries
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-timeentries

# Build webapi service image
cd ../uservice-webapi
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-webapi

# Build test runner
cd ../smoke-test
docker-compose build test-runner
```

## Troubleshooting

### Services not starting

Check logs:
```bash
make logs
# or
docker-compose logs -f
```

### Tests failing

View test runner logs:
```bash
docker-compose logs test-runner
```

### Port conflicts

If ports 1433, 8080, 8081, or 3000 are already in use, modify the port mappings in docker-compose.yml.

### Images not found

Make sure to build the service images first:
```bash
make build
```
