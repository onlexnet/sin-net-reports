# Smoke Test - Local Development Stack

This directory contains the complete local development stack for running smoke tests and local development of the SinNet Reports application.

## Overview

The local stack includes:
- **SQL Server** - Database for time entries
- **Dapr Placement Service** - Service discovery for Dapr sidecars
- **TimeEntries Service** - Core time tracking microservice with Dapr sidecar
- **WebAPI Service** - GraphQL API gateway with Dapr sidecar
- **Static WebApp** - React frontend served by nginx

## Quick Start

### Start the entire stack:

```bash
cd smoke-test
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

# Stop and remove containers and volumes (clean slate)
docker compose down -v
```

## Directory Contents

- `docker-compose.yml` - Main orchestration file for all services
- `dapr-local/` - Dapr configuration for local development
  - `config.yaml` - Dapr runtime configuration

## Build Context

All services are built with the parent directory (`..`) as the build context to access:
- `api/client-java` - Shared gRPC libraries
- `api/schema` - Proto definitions
- Service source code in their respective directories

## Troubleshooting

For detailed troubleshooting and service-specific information, see [LOCAL_STACK.md](../LOCAL_STACK.md) in the root directory.

## Environment Details

- Database: SQL Server 2022, using `tempdb` database
- Spring Profile: `dev` (relaxed security, auto-schema creation)
- Dapr Version: 1.12.3
- Network: Bridge network `sinnet-network`
