# Migration: Application Insights Agent to Init Container

## Overview
This document describes the migration of the Application Insights Java agent from being bundled in the Docker image to being loaded at runtime via an init container in Azure Container Apps.

## Date
February 8, 2026

## Problem Statement
Previously, the Application Insights agent JAR file (~6MB) was downloaded during the CI/CD build process and included in the Docker image. This approach had several drawbacks:
- Larger Docker image size
- Agent version updates required rebuilding the entire application
- Tight coupling between application code and monitoring infrastructure
- Violated separation of concerns

## Solution
Move the Application Insights agent to an init container that downloads the agent at runtime to an ephemeral volume shared with the main application container.

## Changes Made

### 1. GitHub Actions Workflow (`.github/workflows/uservice-timeentries.yml`)
**Removed:**
- Download of Application Insights agent during build process
- wget command that downloaded `applicationinsights-agent-3.7.6.jar`

**Result:** The build process no longer includes the agent in the Docker image.

### 2. Maven Configuration (`uservice-timeentries/host/pom.xml`)
**Removed:**
- `BPE_DELIM_JAVA_TOOL_OPTIONS` buildpack environment variable
- `BPE_PREPEND_JAVA_TOOL_OPTIONS` configuration pointing to embedded agent

**Result:** The Docker image no longer expects or activates an embedded agent.

### 3. Terraform Configuration (`infra/shared/module_container_app_timeentries/main.tf`)
**Added:**
- Volume definition: `appinsights-agent` with `EmptyDir` storage type
- Init container: `download-appinsights-agent`
  - Uses `busybox:latest` image
  - Downloads Application Insights agent 3.7.6 to shared volume
  - CPU: 0.25, Memory: 0.5Gi
- Volume mount in init container: `/appinsights`
- Volume mount in main container: `/appinsights`
- Environment variable: `JAVA_TOOL_OPTIONS=-javaagent:/appinsights/applicationinsights-agent.jar`

**Result:** At runtime, the init container downloads the agent before the main container starts, and the main container loads it from the shared volume.

### 4. Documentation Updates
- Updated ADR-006 (Observability Strategy) to reflect new implementation
- Updated README in `uservice-timeentries/host/src/main/resources/applicationinsights/`
- Updated `.github/copilot-instructions.md` with new build process

### 5. Cyclic Build Workflow (`.github/workflows/cyclic-build.yml`)
**Removed:**
- Application Insights agent download for timeentries service (no longer needed)

## Benefits
1. **Smaller Docker Images**: ~6MB reduction per image
2. **Easier Updates**: Agent version can be updated in Terraform without rebuilding the application
3. **Better Separation**: Monitoring infrastructure separated from application code
4. **Flexibility**: Different environments can use different agent versions if needed
5. **Best Practices**: Follows Azure Container Apps patterns for init containers

## Deployment Considerations
1. **First Deployment**: When deploying this change, the container will start with the init container running first
2. **Rollback**: If needed, rollback requires reverting all changes (Terraform + build pipeline)
3. **Monitoring**: Verify that Application Insights telemetry continues to work after deployment
4. **Init Container Logs**: Check init container logs to ensure agent downloads successfully

## Testing
1. **Terraform Validation**: ✅ Passed with `terraform validate`
2. **YAML Validation**: ✅ Workflow files validated successfully
3. **POM XML Validation**: ✅ Maven configuration is syntactically correct

## Verification Steps (Post-Deployment)
1. Deploy updated Terraform configuration
2. Verify init container completes successfully: `az containerapp logs show --name sinnet-timeentries --resource-group sinnet-env-prd01 --container download-appinsights-agent`
3. Verify main container starts successfully
4. Check Application Insights for telemetry from the timeentries service
5. Verify the agent is loaded: Check container logs for Java agent startup messages

## Related Resources
- [Azure Container Apps Init Containers](https://learn.microsoft.com/en-us/azure/container-apps/init-containers)
- [Application Insights Java Agent](https://learn.microsoft.com/en-us/azure/azure-monitor/app/java-in-process-agent)
- [Terraform azurerm_container_app](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/resources/container_app)

## Notes
- The webapi service still includes the agent in its Docker image (different approach not yet migrated)
- This change only affects the timeentries service
- The agent version (3.7.6) is hardcoded in the init container command and should be parameterized in future improvements
