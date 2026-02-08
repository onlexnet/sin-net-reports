Context: SinNet Reports microservices application needs comprehensive observability covering traces, metrics, logs, and user monitoring across React frontend and Java Spring Boot backend services

Decision: We decided to use Azure Application Insights as the unified observability platform

Solutions:
- **Application Insights**: Native Azure integration with automatic instrumentation for Java and JavaScript SDKs. Provides end-to-end tracing, user tracking, performance monitoring, and log aggregation
- **OpenTelemetry OTLP**: Vendor-neutral observability with OTLP collectors and exporters. Would require additional infrastructure setup and configuration complexity
- **Prometheus + Grafana**: Separate metrics collection with custom dashboards. Would duplicate functionality already available in Application Insights

Reason: Application Insights provides zero-configuration integration with Azure Container Apps, automatic log forwarding from containers, native Java agent auto-instrumentation, React SDK with user tracking, and cost-effective unified platform for all observability needs. OpenTelemetry would add complexity without significant benefits for Azure-native deployment

Consequences: 
- **Positive**: Automatic instrumentation with minimal code changes, end-to-end distributed tracing from React to database, email-based user journey tracking, native Azure integration with zero-config log forwarding, single tool for traces/metrics/logs/alerts
- **Negative**: Vendor lock-in with Azure ecosystem, limited customization compared to OpenTelemetry flexibility, dependency on GitHub Secrets for connection string management
- **Implementation**: Frontend uses `@microsoft/applicationinsights-web` v3.3.4 with automatic route tracking and authenticated user context. Backend uses Application Insights Java Agent v3.7.6 loaded via init container in Azure Container Apps. Infrastructure uses Terraform to provision Application Insights resource and distribute connection strings via Container Apps environment variables

Notes:
- Java agent is downloaded at runtime by an init container to an ephemeral volume shared with the main application container
- The main container activates the agent via JAVA_TOOL_OPTIONS environment variable pointing to the shared volume
- Connection string is distributed from Terraform → KeyVault → Container Apps for backend, and GitHub Secrets → CI/CD → runtime-config.json for frontend
- Automatic correlation between React user actions, GraphQL queries, gRPC calls, and database operations
- Log forwarding: Console output → Container Apps → Log Analytics Workspace → Application Insights
- Current status: Infrastructure and SDK integration complete, agent activation configured via init container, alerting rules and dashboards pending