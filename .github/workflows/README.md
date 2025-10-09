# GitHub Workflows

This directory contains automated CI/CD workflows for the sin-net-reports project.

## Workflows

### Production Workflows (with deployment)
- **`uservice-timeentries.yml`** - Builds and deploys the timeentries microservice
- **`uservice-webapi.yml`** - Builds and deploys the webapi microservice  
- **`static-webapp.yml`** - Builds and deploys the static web application

### Maintenance Workflows (no deployment)
- **`cyclic-build.yml`** - Cyclic build verification without deployment

## Cyclic Build Workflow

The `cyclic-build.yml` workflow was created to address dependency-related build failures by running regular builds without any deployments.

### Purpose
- **Early Detection**: Catch build failures caused by dependency changes
- **No Risk**: Builds only, no deployments to production
- **Comprehensive**: Tests all major components (Java services, static webapp)
- **Monitoring**: Checks for available dependency and plugin updates

### Schedule
- Runs daily at 02:00 UTC to avoid peak hours
- Can be manually triggered for testing

### Components Tested
1. **Java Components**:
   - gRPC API Client (`api/client-java`)
   - Timeentries Service (`uservice-timeentries`)
   - WebAPI Service (`uservice-webapi`)

2. **Frontend**:
   - Static WebApp (`static-webapp`)

3. **Dependency Checks**:
   - Maven dependency updates
   - Maven plugin updates

### Benefits
- **Proactive**: Issues are detected before they affect development
- **Non-disruptive**: No impact on production systems
- **Informative**: Provides visibility into outdated dependencies
- **Reliable**: Uses the same build steps as production workflows