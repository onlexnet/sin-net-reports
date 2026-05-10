"""Azure Functions app entrypoint for app_raport1.

This module defines minimal HTTP endpoints used for health checks and
manual report-response testing.
"""

from __future__ import annotations

import json
from typing import Any

import azure.functions as func

app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)

@app.route(route="health", methods=["GET"], auth_level=func.AuthLevel.ANONYMOUS)
def health(req: func.HttpRequest) -> func.HttpResponse:
    """Return a basic health response for smoke checks."""
    _ = req  # Request is unused but kept for signature consistency.

    payload: dict[str, Any] = {
        "status": "ok",
        "service": "app_raport1",
    }
    return func.HttpResponse(
        body=json.dumps(payload),
        status_code=200,
        mimetype="application/json",
    )


@app.route(route="report", methods=["GET"], auth_level=func.AuthLevel.ANONYMOUS)
def report(req: func.HttpRequest) -> func.HttpResponse:
    """Return deterministic sample report JSON for manual verification."""
    _ = req

    payload: dict[str, Any] = {
        "reportId": "sample-001",
        "generatedAt": "2026-01-01T00:00:00Z",
        "currency": "PLN",
        "summary": {
            "hours": 40.0,
            "billableAmount": 5000.0,
            "entries": 5,
        },
        "items": [
            {
                "day": "2026-01-05",
                "project": "Internal",
                "hours": 8.0,
                "rate": 125.0,
                "amount": 1000.0,
            },
            {
                "day": "2026-01-06",
                "project": "Internal",
                "hours": 8.0,
                "rate": 125.0,
                "amount": 1000.0,
            },
            {
                "day": "2026-01-07",
                "project": "Internal",
                "hours": 8.0,
                "rate": 125.0,
                "amount": 1000.0,
            },
            {
                "day": "2026-01-08",
                "project": "Internal",
                "hours": 8.0,
                "rate": 125.0,
                "amount": 1000.0,
            },
            {
                "day": "2026-01-09",
                "project": "Internal",
                "hours": 8.0,
                "rate": 125.0,
                "amount": 1000.0,
            },
        ],
    }
    return func.HttpResponse(
        body=json.dumps(payload),
        status_code=200,
        mimetype="application/json",
    )
