# Smoke Test - Local Development Stack

This directory contains the local k3d/k3s setup used for smoke tests and day-to-day development.

## Quick Start

```bash
cd smoke-test
./install-prerequisites.sh  # First time only
./setup-k3d.sh up
```

## Access

- **Dapr Dashboard**: http://localhost:18080
- **Frontend**: http://localhost:3000
- **WebAPI GraphQL**: http://localhost:11031/graphiql
- **TimeEntries health**: http://localhost:11021/actuator/health
- **SQL Server**: localhost:1433 (sa / P@ssw0rd123!)

## Day-to-day Commands

```bash
# Rebuild images and redeploy services
./setup-k3d.sh deploy

# Stream service logs
./setup-k3d.sh logs

# Stop and remove the k3d cluster
./setup-k3d.sh down
```

## Directory Contents

- `setup-k3d.sh` - Cluster lifecycle and deployment helper
- `install-prerequisites.sh` - Installs local tooling (`k3d`, `kubectl`, `helm`)
- `k8s/` - Kubernetes manifests for SQL Server, Dapr components, and services
- `dapr-local/` - Dapr local configuration
- `e2e/` - Browser-based E2E smoke tests (pytest-bdd + Playwright)

## E2E Smoke Tests

```bash
# 1) Start stack
cd smoke-test
./setup-k3d.sh up

# 2) Run tests in another terminal
cd smoke-test/e2e
pip install -r requirements.txt
playwright install
pytest
```

For test details, see [e2e/README.md](e2e/README.md).
