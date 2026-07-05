# Local Development Stack

**Location**: `e2e_tests/`

SinNet local development uses **k3d (k3s in Docker) with Dapr**.

## Lightweight Frontend Development

For frontend-only work, run the React app against production without k3d:
`cd app-time && npm start`. See
[app-time/RUNTIME_CONFIG.md](app-time/RUNTIME_CONFIG.md) for configuration
details.

## Quick Start

```bash
cd e2e_tests
./install-prerequisites.sh  # First time only
./setup-k3d.sh up
```

## Access Points

- **Dapr Dashboard**: http://localhost:18080
- **Frontend**: http://localhost:3000
- **GraphQL API**: http://localhost:11031/graphiql
- **TimeEntries health**: http://localhost:11021/actuator/health
- **SQL Server**: localhost:1433 (sa / P@ssw0rd123!)

## Prerequisites

- Docker 20.10+
- 8GB RAM available for Docker
- `kubectl` (included in devcontainer)
- `k3d` and `helm` (installed by `install-prerequisites.sh`)

## Lifecycle Commands

```bash
# Start cluster + deploy all services
./setup-k3d.sh up

# Rebuild images and redeploy to an existing cluster
./setup-k3d.sh deploy

# Tail all service logs
./setup-k3d.sh logs

# Destroy the cluster
./setup-k3d.sh down
```

## Development Workflow

1. Edit code in:
   - `svc_timeentries/`
   - `svc_webapi/`
   - `app-time/`
2. Rebuild and redeploy:
   ```bash
   cd e2e_tests
   ./setup-k3d.sh deploy
   ```
3. Check status/logs:
   ```bash
   kubectl get pods -n sinnet
   kubectl logs -n sinnet -l app=timeentries --all-containers=true --tail=200
   kubectl logs -n sinnet -l app=webapi --all-containers=true --tail=200
   ```

## Troubleshooting

### Services not ready

```bash
kubectl get pods -n sinnet
kubectl describe pod -n sinnet <pod-name>
kubectl logs -n sinnet <pod-name> --all-containers=true --tail=200
```

### SQL Server issues

```bash
kubectl logs -n sinnet -l app=sqlserver --tail=200
kubectl get svc -n sinnet sqlserver
```

### Dapr issues

```bash
kubectl get pods -n dapr-system
kubectl logs -n dapr-system -l app=dapr-placement-server --tail=200
kubectl logs -n sinnet -l app=timeentries -c daprd --tail=200
kubectl logs -n sinnet -l app=webapi -c daprd --tail=200
```

## Related Documentation

- [e2e_tests/K3D_SETUP.md](e2e_tests/K3D_SETUP.md)
- [e2e_tests/README.md](e2e_tests/README.md)
- [README.md](README.md)
