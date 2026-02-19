# Local Development with k3d (k3s in Docker)

This directory contains a complete Kubernetes-based local development stack using [k3d](https://k3d.io/) (k3s in Docker).

## Why k3d?

- **Dapr integration works out-of-the-box** - no sidecar configuration issues
- **Dashboard works perfectly** - automatically discovers all services
- **Fast startup and low footprint** - k3s is lightweight (~50MB)
- **Production parity** - Azure Container Apps uses Kubernetes underneath
- **Built-in LoadBalancer** - no need for NodePort workarounds
- **Easier port mappings** - simple CLI flags
- **Better observability** - kubectl, logs, metrics built-in

## Prerequisites

Install these tools:

```bash
# Run the automated installer
./install-prerequisites.sh
```

This installs:
- **kubectl** - Kubernetes CLI
- **k3d** - k3s in Docker
- **Helm** - Package manager for Kubernetes

## Quick Start

### 1. Start the cluster

```bash
cd smoke-test
./setup-k3d.sh up
```

This will:
- Create k3d cluster with port mappings
- Install Dapr runtime + Dashboard
- Deploy SQL Server
- Build and deploy all microservices (TimeEntries, WebAPI, Frontend)
- Wait for everything to be ready

**First run takes ~8 minutes** (building images, downloading Dapr).  
**Subsequent runs take ~2 minutes** (cached images, k3d starts quickly).

### 2. Access the services

Once setup completes:

- **Dapr Dashboard**: http://localhost:18080 ← See all services and sidecars!
- **Frontend**: http://localhost:3000
- **WebAPI GraphQL**: http://localhost:11031/graphiql
- **TimeEntries**: http://localhost:11021/actuator/health
- **SQL Server**: `localhost:1433` (sa / P@ssw0rd123!)

### 3. View logs

```bash
# All services
./setup-k3d.sh logs

# Specific pod
kubectl logs -n sinnet -l app=timeentries -f

# Dapr sidecar logs
kubectl logs -n sinnet <pod-name> -c daprd -f
```

### 4. Stop the cluster

```bash
./setup-k3d.sh down
```

This destroys the cluster and frees all resources.

## Development Workflow

### Making changes to code

1. Edit code in `uservice-timeentries/`, `uservice-webapi/`, or `static-webapp/`
2. Rebuild and reload the image:

```bash
# From smoke-test directory
cd ..

# Rebuild specific service
docker build -t sinnet/uservice-timeentries:local -f uservice-timeentries/Dockerfile .
k3d image import sinnet/uservice-timeentries:local --cluster sinnet-local

# Force pod restart to pick up new image
kubectl rollout restart deployment/timeentries -n sinnet
```

### Debugging

```bash
# Get all pods
kubectl get pods -n sinnet

# Describe pod (see events, errors)
kubectl describe pod <pod-name> -n sinnet

# Get logs (app container)
kubectl logs -n sinnet <pod-name> -f

# Get logs (Dapr sidecar)
kubectl logs -n sinnet <pod-name> -c daprd -f

# Port forward to specific pod
kubectl port-forward -n sinnet <pod-name> 8080:8080

# Execute command in pod
kubectl exec -it -n sinnet <pod-name> -- /bin/bash
```

### Checking Dapr communication

```bash
# See Dapr apps
kubectl get configurations,components -n sinnet

# Check Dapr dashboard
# Visit http://localhost:18080
# You'll see:
# - uservice-timeentries
# - uservice-webapi
# - Their health status
# - Metadata
```

## Architecture in k3d

```
┌───────────────────────────────────────────────────────┐
│  k3d Node (Docker Container - k3s server)             │
│                                                       │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Namespace: dapr-system                         │  │
│  │  - dapr-operator                                │  │
│  │  - dapr-sidecar-injector                        │  │
│  │  - dapr-placement-server                        │  │
│  │  - dapr-dashboard ← YOU ACCESS THIS             │  │
│  └─────────────────────────────────────────────────┘  │
│                                                       │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Namespace: sinnet                              │  │
│  │                                                 │  │
│  │  Pod: static-webapp-xxx                         │  │
│  │    └─ Container: static-webapp (nginx)          │  │
│  │                                                 │  │
│  │  Pod: webapi-xxx                                │  │
│  │    ├─ Container: webapi (Spring Boot)           │  │
│  │    └─ Container: daprd (injected automatically) │  │
│  │                                                 │  │
│  │  Pod: timeentries-xxx                           │  │
│  │    ├─ Container: timeentries (Spring Boot)      │  │
│  │    └─ Container: daprd (injected automatically) │  │
│  │                                                 │  │
│  │  Pod: sqlserver-xxx                             │  │
│  │    └─ Container: sqlserver                      │  │
│  └─────────────────────────────────────────────────┘  │
│                                                       │
│  Built-in LoadBalancer (servicelb):                   │
│  - 30000 → 3000 (frontend)                            │
│  - 30031 → 11031 (webapi)                             │
│  - 30021 → 11021 (timeentries)                        │
│  - 30080 → 18080 (dapr dashboard)                     │
│  - 31433 → 1433 (sql server)                          │
└───────────────────────────────────────────────────────┘
         ↓ Exposed to localhost
    Your browser/SQL client
```

## Why k3d for Local Stack

- **Dapr sidecars** are auto-injected via annotations.
- **Dashboard** works out of the box on localhost.
- **Service discovery** uses Kubernetes DNS and Dapr invocation.
- **Logs/observability** are available via `kubectl logs`, `kubectl describe`, and events.
- **Production parity** is high (same deployment model family as Azure Container Apps).

## k3d Characteristics

| Metric | k3d |
|--------|-----|
| **Base distribution** | k3s (lightweight) |
| **Image size** | ~50MB |
| **Memory usage** | ~800MB |
| **Startup time** | ~15s |
| **LoadBalancer** | Built-in (servicelb) |
| **Configuration** | CLI flags |
| **Port mapping** | Simple |

## Troubleshooting

### Dashboard shows no apps

```bash
# Check Dapr is installed
kubectl get pods -n dapr-system

# Check app annotations
kubectl get pod -n sinnet <pod-name> -o yaml | grep dapr.io

# Restart pod to re-inject sidecar
kubectl delete pod -n sinnet <pod-name>
```

### Services not communicating

```bash
# Check Dapr sidecar logs
kubectl logs -n sinnet <pod-name> -c daprd

# Verify app-id matches
kubectl get pod -n sinnet <pod-name> -o jsonpath='{.metadata.annotations.dapr\.io/app-id}'

# Test Dapr invocation manually
kubectl exec -n sinnet <webapi-pod> -- curl http://localhost:3500/v1.0/invoke/uservice-timeentries/method/actuator/health
```

### SQL Server not accessible

```bash
# Check if pod is running
kubectl get pod -n sinnet -l app=sqlserver

# Check logs
kubectl logs -n sinnet -l app=sqlserver

# Port forward for direct access
kubectl port-forward -n sinnet svc/sqlserver 1433:1433
```

### Out of disk space

```bash
# Clean up old images
docker system prune -a

# Restart cluster
./setup-k3d.sh down
./setup-k3d.sh up
```

### Port already in use

```bash
# Check what's using the port
sudo lsof -i :3000

# If it's another k3d cluster
k3d cluster list
k3d cluster delete <name>
```

## Files Structure

```
smoke-test/
├── setup-k3d.sh                # Main automation script
├── install-prerequisites.sh    # Install k3d, kubectl, helm
└── k8s/                        # Kubernetes manifests
    ├── dapr-config.yaml        # Dapr configuration
    ├── dapr-dashboard-service.yaml  # Dashboard LoadBalancer service
    ├── sqlserver.yaml          # SQL Server deployment + service
    ├── timeentries.yaml        # TimeEntries + Dapr sidecar
    ├── webapi.yaml             # WebAPI + Dapr sidecar
    └── static-webapp.yaml      # React frontend
```

## Next Steps

- Add integration tests that run against k3d cluster
- Add scripts for updating single service without full rebuild
- Consider adding Prometheus/Grafana for metrics
- Add CI/CD pipeline to test against k3d

## Resources

- [k3d Documentation](https://k3d.io/)
- [k3s Documentation](https://k3s.io/)
- [Dapr on Kubernetes](https://docs.dapr.io/operations/hosting/kubernetes/)
- [Dapr Dashboard](https://docs.dapr.io/reference/cli/dapr-dashboard/)
