"""Azure Functions app entrypoint for app_raport1.

This module defines HTTP endpoints based on OpenAPI specification
using FastAPI for contract-first API development with automatic
request/response validation.
"""

from __future__ import annotations

from datetime import UTC, datetime

import azure.functions as func
from azure.functions import AsgiMiddleware
from fastapi import FastAPI

from models_generated import (
    HealthResponse,
    ReportItem,
    ReportResponse,
    ReportSummary,
    Status,
)

# FastAPI application instance
fastapi_app = FastAPI(
    title="Fun Report1 API",
    description=(
        "Azure Function app providing health checks and sample report generation"
    ),
    version="1.0.0",
    openapi_url="/api/openapi.json",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
)


@fastapi_app.get(
    "/health",
    response_model=HealthResponse,
    tags=["Health"],
    summary="Health check endpoint",
    operation_id="getHealth",
)
async def health() -> HealthResponse:
    """Return a basic health response for smoke checks."""
    return HealthResponse(status=Status.ok, service="app_raport1")


@fastapi_app.get(
    "/report",
    response_model=ReportResponse,
    tags=["Reports"],
    summary="Generate sample report",
    operation_id="getReport",
)
async def report() -> ReportResponse:
    """Return deterministic sample report JSON for manual verification."""
    summary = ReportSummary(
        hours=40.0,
        billableAmount=5000.0,
        entries=5,
    )

    items = [
        ReportItem(
            day=datetime(2026, 1, 5, tzinfo=UTC).date(),
            project="Internal",
            hours=8.0,
            rate=125.0,
            amount=1000.0,
        ),
        ReportItem(
            day=datetime(2026, 1, 6, tzinfo=UTC).date(),
            project="Internal",
            hours=8.0,
            rate=125.0,
            amount=1000.0,
        ),
        ReportItem(
            day=datetime(2026, 1, 7, tzinfo=UTC).date(),
            project="Internal",
            hours=8.0,
            rate=125.0,
            amount=1000.0,
        ),
        ReportItem(
            day=datetime(2026, 1, 8, tzinfo=UTC).date(),
            project="Internal",
            hours=8.0,
            rate=125.0,
            amount=1000.0,
        ),
        ReportItem(
            day=datetime(2026, 1, 9, tzinfo=UTC).date(),
            project="Internal",
            hours=8.0,
            rate=125.0,
            amount=1000.0,
        ),
    ]

    return ReportResponse(
        reportId="sample-001",
        generatedAt=datetime(2026, 1, 1, 0, 0, 0, tzinfo=UTC),
        currency="PLN",
        summary=summary,
        items=items,
    )


# Azure Functions HTTP trigger bridging requests to FastAPI.
app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)
asgi_middleware: AsgiMiddleware = AsgiMiddleware(fastapi_app)


@app.function_name(name="http_app_func")
@app.route(route="{*route}")
async def http_app_func(
    req: func.HttpRequest, context: func.Context
) -> func.HttpResponse:
    """Handle all HTTP routes by delegating to the FastAPI ASGI app."""
    return await asgi_middleware.handle_async(req, context)
