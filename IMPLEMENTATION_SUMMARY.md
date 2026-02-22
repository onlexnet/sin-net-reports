# Local Stack Implementation Summary

## Overview
Local development and smoke testing now use **k3d/k3s only**.

The stack is deployed through `smoke-test/setup-k3d.sh`, which provisions:
- k3d cluster
- Dapr control plane + dashboard
- SQL Server
- TimeEntries service
- WebAPI service
- Static WebApp

## Operational Commands

```bash
cd smoke-test

# create cluster and deploy all services
./setup-k3d.sh up

# rebuild images and redeploy into existing cluster
./setup-k3d.sh deploy

# inspect logs
./setup-k3d.sh logs

# destroy cluster
./setup-k3d.sh down
```

## Access Points

- Dapr Dashboard: http://localhost:18080
- Frontend: http://localhost:3000
- WebAPI GraphQL: http://localhost:11031/graphiql
- TimeEntries health: http://localhost:11021/actuator/health
- SQL Server: localhost:1433

## Notes

- Legacy local stack paths were removed in favor of k3d/k3s.
- Documentation and setup references now point to k3d/k3s paths.
- For detailed setup and troubleshooting, use [LOCAL_STACK.md](LOCAL_STACK.md) and [smoke-test/K3D_SETUP.md](smoke-test/K3D_SETUP.md).
