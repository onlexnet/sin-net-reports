# Observability: OpenTelemetry & Application Insights

## TL;DR

The Java backend services (`svc_webapi`, `svc_timeentries`) already use
**OpenTelemetry** under the hood. There is no pending "migrate to
OpenTelemetry" work item for these services — see rationale below.

## Java services (`svc_webapi`, `svc_timeentries`)

Both services are instrumented via the Microsoft-provided
`applicationinsights-agent-<version>.jar`, attached as a JVM `-javaagent`
(downloaded by an init container — see
`infra/shared/module_container_app_webapi/main.tf` and
`infra/shared/module_container_app_timeentries/main.tf`).

As of v3.x, this agent **is** Microsoft's official "Azure Monitor
OpenTelemetry Distro for Java": it is built on top of the standard
[OpenTelemetry Java auto-instrumentation agent](https://opentelemetry.io/docs/zero-code/java/agent/)
and simply ships a pre-wired Azure Monitor exporter, rather than being a
separate/legacy proprietary SDK. See Microsoft's own documentation:
["Enable OpenTelemetry in Application Insights"](https://learn.microsoft.com/en-us/azure/azure-monitor/app/opentelemetry-enable?tabs=java),
which lists `applicationinsights-agent-*.jar` as **the** Java package to use
for OpenTelemetry-based collection.

There is currently no Microsoft-supported way to attach the Azure Monitor
exporter to the *vanilla* `opentelemetry-javaagent.jar` without adding a
separate OpenTelemetry Collector (running the `azuremonitor` exporter from
`opentelemetry-collector-contrib`) as new infrastructure. Given Azure Monitor
remains our telemetry backend, this repo intentionally keeps the
`applicationinsights-agent-*.jar`.

### Agent version

The agent version is pinned via the `applicationinsights_agent_version`
Terraform variable in each container app module (currently `3.7.9`, the
latest available at time of writing). Bump this variable when newer agent
releases are available:
[ApplicationInsights-Java releases](https://github.com/microsoft/ApplicationInsights-Java/releases).

### Standard `OTEL_*` environment variable passthrough

Because the agent is OpenTelemetry-based, it honors a subset of the standard
OpenTelemetry SDK autoconfiguration environment variables in addition to the
required `APPLICATIONINSIGHTS_CONNECTION_STRING`, for example:

- `OTEL_SERVICE_NAME` — override the reported service name
- `OTEL_RESOURCE_ATTRIBUTES` — attach extra resource attributes (e.g.
  `deployment.environment=prod`)
- `OTEL_TRACES_SAMPLER` / `OTEL_TRACES_SAMPLER_ARG` — sampling configuration

These give a partial "escape hatch" for standard OpenTelemetry configuration
without abandoning Azure Monitor as the backend. If the team later decides to
move off Azure Monitor entirely (e.g. to Grafana/Tempo/Jaeger or another OTLP
backend), that requires introducing a real OpenTelemetry Collector and is a
separate, larger effort — not currently planned.

## Frontend (`app_time`)

The React app uses `@microsoft/applicationinsights-web`
(`src/app/configuration/ApplicationInsights.ts`) for browser telemetry (page
views, user context, correlation with backend requests).

This is intentionally **not** being migrated to OpenTelemetry: Azure's
OpenTelemetry exporter for JavaScript (`@azure/monitor-opentelemetry-exporter`)
is explicitly Node.js-only and does not support browser environments. As long
as Azure Monitor remains the telemetry backend, `@microsoft/applicationinsights-web`
is the correct and only officially supported client for browser telemetry.

## Azure Function (`fun_report1`)

Uses `host.json`'s built-in `applicationInsights` logging configuration for
the Azure Functions host. This is unaffected by/unrelated to the above and is
not in scope for any OpenTelemetry migration work.
