# SinNet Reports Microservices Application

SinNet Reports is a microservices-based time tracking and reporting application built with Java Spring Boot backend services, React TypeScript frontend, and comprehensive testing infrastructure. The application manages time entries, customer data, and generates PDF reports.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites and Environment Setup
Install the required development tools in this exact order:

```bash
# Install Java 21 (required for all services)
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Install Node.js 22.12.0 using nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
nvm install 22.12.0
nvm use 22.12.0

# Verify installation
java -version  # Should show OpenJDK 21
node --version # Should show v22.12.0
npm --version  # Should show 10.9.0
mvn -version   # Should show Maven 3.9+
docker --version
docker compose version
```

### Core Build Process (NEVER CANCEL - Total time: ~3 minutes)
Build all components in this exact order. NEVER CANCEL builds - they may take longer than expected but will complete:

```bash
# 1. Download ApplicationInsights agent for timeentries service
cd uservice-timeentries
pushd .
cd host/src/main/resources/applicationinsights
export APPLICATIONINSIGHTS_AGENT_VERSION=3.7.6
wget --no-verbose https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APPLICATIONINSIGHTS_AGENT_VERSION}/applicationinsights-agent-${APPLICATIONINSIGHTS_AGENT_VERSION}.jar
mv applicationinsights-agent-${APPLICATIONINSIGHTS_AGENT_VERSION}.jar applicationinsights-agent.jar
popd

# 2. Build timeentries microservice (~15 seconds - NEVER CANCEL)
export SEMVERSION=$(cat .version)
mvn -ntp install -pl host -am -DskipTests
cd ..

# 3. Download ApplicationInsights agent for webapi service
cd uservice-webapi
pushd .
cd host/src/main/resources/applicationinsights
export APPLICATIONINSIGHTS_AGENT_VERSION=3.7.6
wget --no-verbose https://github.com/microsoft/ApplicationInsights-Java/releases/download/${APPLICATIONINSIGHTS_AGENT_VERSION}/applicationinsights-agent-${APPLICATIONINSIGHTS_AGENT_VERSION}.jar
mv applicationinsights-agent-${APPLICATIONINSIGHTS_AGENT_VERSION}.jar applicationinsights-agent.jar
popd

# 4. Build webapi microservice (~40 seconds - NEVER CANCEL)
export SEMVERSION=$(cat .semversion)
mvn -ntp install -Drevision=$SEMVERSION -DskipTests  # -DskipTests also skips JaCoCo
cd ..

# 5. Build React frontend (~65 seconds total - NEVER CANCEL)
cd static-webapp
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
nvm use 22.12.0

# Install dependencies (~20 seconds - NEVER CANCEL)
npm install

# Generate GraphQL models (~1 second)
npm run generate

# Build React app (~45 seconds - NEVER CANCEL)
export MY_VERSION=$(cat .version)
npm config set allow-same-version true
npm version $MY_VERSION
npm run build
cd ..
```

### Docker Image Creation (~50 seconds each - NEVER CANCEL)
Build Docker images using Spring Boot buildpacks:

```bash
# Build timeentries service image
cd uservice-timeentries
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-timeentries
cd ..

# Build webapi service image  
cd uservice-webapi
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-webapi
cd ..
```

### Running Tests
**IMPORTANT**: Tests require Testcontainers and may fail in some Docker environments. Use `-DskipTests` for builds.

```bash
# Run unit tests (may fail due to Testcontainers issues)
cd uservice-timeentries
mvn test -pl host
cd ../uservice-webapi  
mvn test -pl host
cd ..

# Run React tests
cd static-webapp
npm test
cd ..
```

## Validation

### Critical Build Validation Steps
Always run these steps after making code changes:

```bash
# 1. Validate Java services compile without errors
mvn clean compile -f api/client-java
mvn clean compile -pl host -f uservice-timeentries
mvn clean compile -pl host -f uservice-webapi

# 2. Validate React app builds without errors
cd static-webapp
npm run build
cd ..

# 3. Check for linting issues (run these before committing)
# Java services use Checkstyle (automatically run during build)
# React app uses ESLint (warnings shown during build, errors fail build)
```

### Manual Validation Scenarios
After making changes, test these scenarios:

#### Java Services Validation
```bash
# 1. Start timeentries service locally (requires database)
cd uservice-timeentries/host
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Service should start on http://localhost:8080
# Check health endpoint: curl http://localhost:8080/actuator/health

# 2. Start webapi service locally (requires database) 
cd uservice-webapi/host
mvn spring-boot:run -Dspring-boot.run.profiles=dev  
# Service should start and serve GraphQL endpoint
```

#### React Frontend Validation
```bash
cd static-webapp
# Start development server
npm start
# Application should start on http://localhost:3000
# Should display login page (Azure B2C integration)
# Check browser console for errors
```

## Docker Compose Local Development

**NOTE**: The docker-compose.yaml references missing directories. Local development currently requires manual service startup.

```bash
# Current docker-compose.yaml has issues - service-postgres directory missing
# Use individual service startup instead (see Java Services Validation above)
```

## Common Tasks

### Quick Build (Skip Tests)
```bash
# Full build pipeline without tests (~3 minutes total)
# Note: -DskipTests also skips JaCoCo for webapi service
mvn -ntp install -f api/client-java && \
(cd uservice-timeentries && export SEMVERSION=$(cat .version) && mvn -ntp install -pl host -am -DskipTests) && \
(cd uservice-webapi && export SEMVERSION=$(cat .semversion) && mvn -ntp install -Drevision=$SEMVERSION -DskipTests) && \
(cd static-webapp && npm install && npm run generate && npm run build)
```

### Development Profiles
Java services support multiple profiles:
- `dev` - Development profile with relaxed security
- `prod` - Production profile with full security and SQL Server database  
- `test` - Test profile used during testing

```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev -pl host -f uservice-timeentries
```

### Version Management
Each component has its own version file:
- `uservice-timeentries/.version` - Current: 1.0.9
- `uservice-webapi/.semversion` - Current: 0.1.3  
- `static-webapp/.version` - Current: 1.4.3
- `api/client-java` - Current: 0.1.3

## Architecture Overview

### Microservices Structure
- **uservice-timeentries**: Time entry management service
- **uservice-webapi**: GraphQL API gateway service  
- **static-webapp**: React TypeScript frontend
- **test-webapi**: BDD integration tests using Cucumber
- **api/client-java**: Shared gRPC client libraries

### Key Dependencies
- **Java 21**: Runtime for all backend services
- **Spring Boot 3.x**: Framework for microservices
- **React 17**: Frontend framework  
- **GraphQL**: API communication between frontend and backend
- **gRPC**: Inter-service communication
- **Azure B2C**: Authentication provider
- **Testcontainers**: Integration testing (may fail in some environments)

## Troubleshooting

### Common Issues

**Maven dependency issues**: Run `mvn clean install` on dependencies in order (api/client-java)

**Node.js version mismatch**: Ensure Node.js 22.12.0 is active with `nvm use 22.12.0`

**ApplicationInsights agent missing**: Download manually using wget commands shown above

**Testcontainers failures**: Use `-DskipTests` flag for builds - tests require specific Docker setup

**Docker compose failures**: service-postgres directory missing - use individual service startup

**React build warnings**: ESLint warnings shown during build are non-blocking, only errors fail the build

### Environment Variables
Some tests require Azure credentials and KeyVault access:
```bash
# BDD tests need Azure service principal credentials
# Set via init-vars.sh in test-webapi/main/
```

## CI/CD Integration
This repository uses GitHub Actions for CI/CD:
- `.github/workflows/uservice-timeentries.yml`
- `.github/workflows/uservice-webapi.yml`  
- `.github/workflows/static-webapp.yml`

Always ensure local builds match CI requirements before pushing.