"""Unit tests for HTTP endpoints in function_app.

Tests use FastAPI TestClient to validate endpoint behavior,
response schemas, and contract compliance with OpenAPI specification.
"""

from __future__ import annotations

import io
import re
import zipfile

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


def test_generate_report1_zip_endpoint() -> None:
    """POST /api/report1/zip returns a valid ZIP archive with application/zip content-type."""
    payload = {
        "items": [
            {
                "customer": {
                    "customer_id": "cust-001",
                    "customer_name": "Test Firma",
                    "customer_city": "Warszawa",
                    "customer_address": "Testowa 1",
                },
                "activities": [
                    {
                        "description": "Naprawa serwera",
                        "who": "Jan Kowalski",
                        "when": {"year": 2024, "month": 6, "day": 15},
                        "how_long_in_mins": 90,
                        "how_far_in_kms": 15,
                    },
                    {
                        "description": "Konsultacja",
                        "who": "Anna Nowak",
                        "when": {"year": 2024, "month": 6, "day": 16},
                        "how_long_in_mins": 0,
                        "how_far_in_kms": 5,
                    },
                ],
            }
        ]
    }

    response = client.post("/api/report1/zip", json=payload)

    assert response.status_code == 200
    assert "application/zip" in response.headers["content-type"]
    assert response.content[:2] == b"PK", "Response body must be a ZIP archive"


def test_generate_report1_pdf_endpoint_matches_pdf_inside_zip() -> None:
    """POST /api/report1/pdf returns the same PDF bytes as the ZIP endpoint for one customer."""
    single_customer_payload = {
        "customer": {
            "customer_id": "cust-001",
            "customer_name": "Test Firma",
            "customer_city": "Warszawa",
            "customer_address": "Testowa 1",
        },
        "activities": [
            {
                "description": "Naprawa serwera",
                "who": "Jan Kowalski",
                "when": {"year": 2024, "month": 6, "day": 15},
                "how_long_in_mins": 90,
                "how_far_in_kms": 15,
            },
            {
                "description": "Konsultacja",
                "who": "Anna Nowak",
                "when": {"year": 2024, "month": 6, "day": 16},
                "how_long_in_mins": 0,
                "how_far_in_kms": 5,
            },
        ],
    }

    pdf_response = client.post("/api/report1/pdf", json=single_customer_payload)
    assert pdf_response.status_code == 200
    assert "application/pdf" in pdf_response.headers["content-type"]
    assert pdf_response.content.startswith(b"%PDF-")

    zip_response = client.post(
        "/api/report1/zip", json={"items": [single_customer_payload]}
    )
    assert zip_response.status_code == 200
    assert "application/zip" in zip_response.headers["content-type"]

    with zipfile.ZipFile(io.BytesIO(zip_response.content), mode="r") as zf:
        names = zf.namelist()
        assert len(names) == 1
        zipped_pdf = zf.read(names[0])

    # ReportLab embeds a per-document trailer ID; normalize it before comparison.
    id_pattern = rb"/ID\s*\[\s*<[^>]+>\s*<[^>]+>\s*\]"
    normalized_pdf_response = re.sub(id_pattern, b"/ID [<fixed><fixed>]", pdf_response.content)
    normalized_zipped_pdf = re.sub(id_pattern, b"/ID [<fixed><fixed>]", zipped_pdf)
    assert normalized_pdf_response == normalized_zipped_pdf


def test_openapi_docs_available() -> None:
    """OpenAPI documentation endpoints are accessible."""
    # Test OpenAPI JSON spec
    openapi_response = client.get("/api/openapi.json")
    assert openapi_response.status_code == 200
    openapi_spec = openapi_response.json()
    assert "openapi" in openapi_spec
    assert "paths" in openapi_spec
    assert "/api/health" in openapi_spec["paths"]
    assert "/api/report1/pdf" in openapi_spec["paths"]

    # Test Swagger UI
    docs_response = client.get("/api/docs")
    assert docs_response.status_code == 200

    # Test ReDoc
    redoc_response = client.get("/api/redoc")
    assert redoc_response.status_code == 200
