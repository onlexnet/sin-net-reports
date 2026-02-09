# Local Development Stack

This directory contains a complete local development stack for the SinNet Reports application, including all microservices, database, and Dapr runtime.

## Overview

The local stack includes:
- **SQL Server** - Database for time entries
- **Dapr Placement Service** - Service discovery for Dapr sidecars
- **TimeEntries Service** - Core time tracking microservice with Dapr sidecar
- **WebAPI Service** - GraphQL API gateway with Dapr sidecar
- **Static WebApp** - React frontend served by nginx

## Prerequisites

- Docker 20.10+ and Docker Compose V2
- 8GB RAM available for Docker
- Ports available: 1433, 3000, 11021, 11031, 50006

## Quick Start

### Start the entire stack:

```bash
docker compose up --build
```

This single command will:
1. Build all services from source (~5-10 minutes first time)
2. Start SQL Server and wait for it to be healthy
3. Start Dapr placement service
4. Start TimeEntries and WebAPI services with Dapr sidecars
5. Start the React frontend

### Access the services:

- **Frontend**: http://localhost:3000
- **WebAPI GraphQL**: http://localhost:11031/graphiql
- **TimeEntries**: http://localhost:11021/actuator/health
- **SQL Server**: localhost:1433 (sa / P@ssw0rd123!)

### Stop the stack:

```bash
# Stop services (keeps data)
docker compose stop

# Stop and remove containers (keeps data)
docker compose down

# Stop, remove containers and volumes (removes data)
docker compose down -v
```

## Architecture

```
┌─────────────────┐
│  Static WebApp  │  :3000
│   (React/nginx) │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────────┐
│  WebAPI Service │◄────►│ WebAPI Dapr      │
│  (Spring Boot)  │:11031│ Sidecar          │
└────────┬────────┘      └──────────────────┘
         │                        │
         │                        ▼
         │               ┌──────────────────┐
         │               │ Dapr Placement   │
         │               │ Service :50006   │
         │               └──────────────────┘
         │                        ▲
         ▼                        │
┌─────────────────┐      ┌──────────────────┐
│ TimeEntries Svc │◄────►│ TimeEntries Dapr │
│  (Spring Boot)  │:11021│ Sidecar          │
└────────┬────────┘      └──────────────────┘
         │
         ▼
┌─────────────────┐
│   SQL Server    │
│     :1433       │
└─────────────────┘
```

## Development Workflow

### 1. Make code changes

Edit files in:
- `uservice-timeentries/` - TimeEntries service
- `uservice-webapi/` - WebAPI service
- `static-webapp/` - React frontend

### 2. Rebuild specific service

```bash
# Rebuild and restart TimeEntries
docker compose up --build -d timeentries

# Rebuild and restart WebAPI
docker compose up --build -d webapi

# Rebuild and restart Frontend
docker compose up --build -d static-webapp
```

### 3. View logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f timeentries
docker compose logs -f webapi
docker compose logs -f static-webapp
```

### 4. Check health

```bash
# TimeEntries health
curl http://localhost:11021/actuator/health

# WebAPI health
curl http://localhost:11031/actuator/health

# Frontend health
curl http://localhost:3000/health
```

## Service Configuration

### TimeEntries Service
- **Port**: 11021
- **Profile**: dev
- **Database**: SQL Server (tempdb)
- **Dapr App ID**: uservice-timeentries
- **Dapr Ports**: gRPC 50001, HTTP 3601

### WebAPI Service
- **Port**: 11031
- **GraphQL**: /graphql, /graphiql
- **Dapr App ID**: uservice-webapi
- **Dapr Ports**: gRPC 50002, HTTP 3602
- **Upstream**: Connects to TimeEntries via Dapr

### Static WebApp
- **Port**: 3000
- **Type**: React SPA served by nginx
- **Backend**: Points to WebAPI at localhost:11031

## Database Management

### Connect to SQL Server

```bash
docker compose exec sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "P@ssw0rd123!"
```

### Reset database

```bash
# Remove volume to reset database
docker compose down -v
docker compose up -d sqlserver
```

## Troubleshooting

### Service fails to start

1. Check logs: `docker compose logs <service-name>`
2. Verify ports are available: `netstat -an | grep <port>`
3. Check Docker resources (RAM, CPU)

### Database connection issues

1. Wait for SQL Server to be healthy: `docker compose ps`
2. Check database logs: `docker compose logs sqlserver`
3. Verify connection: `docker compose exec sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "P@ssw0rd123!" -Q "SELECT 1"`

### Dapr sidecar issues

1. Check placement service: `docker compose logs dapr-placement`
2. Verify sidecar logs: `docker compose logs timeentries-dapr`
3. Ensure app-id is unique for each service

### Build failures

1. Clear Docker cache: `docker compose build --no-cache`
2. Check disk space: `docker system df`
3. Prune unused data: `docker system prune -a`

### Frontend can't connect to backend

1. Check WebAPI is running: `curl http://localhost:11031/actuator/health`
2. Verify CORS settings in WebAPI
3. Check browser console for errors

## CI/CD Integration

The `.github/workflows/local-stack.yml` workflow builds the entire stack on every push to validate that all services build correctly from source.

### Workflow runs:
1. Maven build for Java services
2. npm build for React app
3. Docker image builds
4. Docker Compose validation

## Performance

### Build times (first build):
- API client: ~20s
- TimeEntries: ~60s
- WebAPI: ~90s
- Frontend: ~80s
- Docker images: ~120s
- **Total**: ~6-8 minutes

### Build times (cached):
- Maven builds: ~10s each
- npm build: ~20s
- Docker images: ~30s each
- **Total**: ~2-3 minutes

### Resource usage:
- SQL Server: ~500MB RAM
- Dapr services: ~100MB RAM each
- Java services: ~512MB RAM each
- Frontend: ~50MB RAM
- **Total**: ~2GB RAM minimum

## Files Structure

```
.
├── docker-compose.yml              # Main orchestration file
├── .dockerignore                  # Build optimization
├── .dapr-local/                   # Dapr configuration for local stack
│   └── config.yaml
├── static-webapp/
│   ├── Dockerfile                 # Frontend multi-stage build
│   └── nginx.conf                 # nginx configuration
├── uservice-timeentries/
│   └── Dockerfile                 # TimeEntries multi-stage build
└── uservice-webapi/
    └── Dockerfile                 # WebAPI multi-stage build
```

## Next Steps

1. **Add database migrations**: Integrate Liquibase for schema management
2. **Add seed data**: Pre-populate database with test data
3. **Add E2E tests**: Integrate Cypress or Playwright
4. **Add monitoring**: Add Prometheus/Grafana for metrics
5. **Add Redis**: Enable Dapr pub/sub for events
6. **Add Zipkin**: Enable distributed tracing

## Related Documentation

- [Main README](README.md)
- [Deployment Guide](DEPLOYMENT.md)
- [ADR 004: Run Local Stack](.docs/ADR_004_run_local_stack.md)
- [Copilot Instructions](.github/copilot-instructions.md)
