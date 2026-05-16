"""Unit tests for HTTP endpoints in function_app.

Tests use FastAPI TestClient to validate endpoint behavior,
response schemas, and contract compliance with OpenAPI specification.
"""

from __future__ import annotations

from datetime import datetime

from fastapi.testclient import TestClient

from src.app import fastapi_app

# Create test client for FastAPI app
client = TestClient(fastapi_app)


def test_health_endpoint_success() -> None:
    """Health endpoint returns expected status and payload."""
    response = client.get("/api/health")

    assert response.status_code == 200
    assert response.headers["content-type"] == "application/json"
    payload = response.json()
    assert payload == {"status": "ok", "service": "app_raport1"}


def test_health_endpoint_schema_compliance() -> None:
    """Health endpoint response complies with OpenAPI schema."""
    response = client.get("/api/health")

    payload = response.json()
    assert "status" in payload
    assert "service" in payload
    assert payload["status"] in ["ok", "degraded", "unhealthy"]
    assert isinstance(payload["service"], str)


def test_report_endpoint_success() -> None:
    """Report endpoint returns expected top-level structure."""
    response = client.get("/api/report")

    assert response.status_code == 200
    payload = response.json()
    assert payload["reportId"] == "sample-001"
    assert payload["currency"] == "PLN"
    assert payload["summary"]["entries"] == 5
    assert len(payload["items"]) == 5


def test_report_content_is_deterministic() -> None:
    """Report payload stays deterministic across repeated calls."""
    first_response = client.get("/api/report")
    second_response = client.get("/api/report")

    first_payload = first_response.json()
    second_payload = second_response.json()

    assert first_payload == second_payload
    assert first_payload["generatedAt"] == "2026-01-01T00:00:00Z"
    assert sum(item["amount"] for item in first_payload["items"]) == first_payload[
        "summary"
    ]["billableAmount"]


def test_report_schema_compliance() -> None:
    """Report endpoint response complies with OpenAPI schema."""
    response = client.get("/api/report")

    payload = response.json()

    # Validate top-level fields
    assert "reportId" in payload
    assert "generatedAt" in payload
    assert "currency" in payload
    assert "summary" in payload
    assert "items" in payload

    # Validate currency pattern (3 uppercase letters)
    assert len(payload["currency"]) == 3
    assert payload["currency"].isupper()

    # Validate timestamp format (ISO 8601)
    datetime.fromisoformat(payload["generatedAt"].replace("Z", "+00:00"))

    # Validate summary structure
    summary = payload["summary"]
    assert "hours" in summary
    assert "billableAmount" in summary
    assert "entries" in summary
    assert summary["hours"] >= 0
    assert summary["billableAmount"] >= 0
    assert summary["entries"] >= 0

    # Validate items array
    items = payload["items"]
    assert isinstance(items, list)
    for item in items:
        assert "day" in item
        assert "project" in item
        assert "hours" in item
        assert "rate" in item
        assert "amount" in item
        assert item["hours"] >= 0
        assert item["rate"] >= 0
        assert item["amount"] >= 0


def test_openapi_docs_available() -> None:
    """OpenAPI documentation endpoints are accessible."""
    # Test OpenAPI JSON spec
    openapi_response = client.get("/api/openapi.json")
    assert openapi_response.status_code == 200
    openapi_spec = openapi_response.json()
    assert "openapi" in openapi_spec
    assert "paths" in openapi_spec
    assert "/api/health" in openapi_spec["paths"]
    assert "/api/report" in openapi_spec["paths"]

    # Test Swagger UI
    docs_response = client.get("/api/docs")
    assert docs_response.status_code == 200

    # Test ReDoc
    redoc_response = client.get("/api/redoc")
    assert redoc_response.status_code == 200
