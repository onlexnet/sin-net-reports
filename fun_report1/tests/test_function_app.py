"""Unit tests for HTTP endpoints in function_app.

Tests use FastAPI TestClient to validate endpoint behavior,
response schemas, and contract compliance with OpenAPI specification.

Blob storage uploads are monkeypatched so tests never touch a real Azure
Storage account; the shared-secret header is exercised explicitly to make
sure unauthenticated/incorrectly authenticated calls are rejected.
"""

from __future__ import annotations

from datetime import UTC, datetime
from typing import Any

import pytest
from fastapi.testclient import TestClient

from src.app import fastapi_app
from src.auth import SHARED_SECRET_HEADER
from src.blob_storage import ReportLink

# Create test client for FastAPI app
client = TestClient(fastapi_app)

TEST_SECRET = "unit-test-shared-secret"


@pytest.fixture(autouse=True)
def _shared_secret_env(monkeypatch: pytest.MonkeyPatch) -> None:
    """Configure the shared secret expected by fun_report1 for every test."""
    monkeypatch.setenv("REPORT1_SHARED_SECRET", TEST_SECRET)


@pytest.fixture
def _stub_upload_report(monkeypatch: pytest.MonkeyPatch) -> list[tuple[bytes, str, str | None]]:
    """Replace blob upload with an in-memory stub and record calls made to it."""
    calls: list[tuple[bytes, str, str | None]] = []

    def fake_upload_report(content: bytes, extension: str, download_filename: str | None = None) -> ReportLink:
        calls.append((content, extension, download_filename))
        return ReportLink(
            url=f"https://stub.blob.core.windows.net/reports/report1/stub.{extension}?sig=stub",
            expires_at=datetime(2026, 1, 1, tzinfo=UTC),
        )

    monkeypatch.setattr("src.blob_storage.upload_report", fake_upload_report)
    return calls


def _auth_headers(secret: str = TEST_SECRET) -> dict[str, str]:
    return {SHARED_SECRET_HEADER: secret}


ZIP_PAYLOAD: dict[str, Any] = {
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

PDF_PAYLOAD: dict[str, Any] = ZIP_PAYLOAD["items"][0]


def test_health_endpoint_success() -> None:
    """Health endpoint returns expected status and payload without any auth header."""
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


def test_generate_report1_zip_requires_shared_secret() -> None:
    """POST /api/report1/zip rejects requests without the shared-secret header."""
    response = client.post("/api/report1/zip", json=ZIP_PAYLOAD)

    assert response.status_code == 401


def test_generate_report1_zip_rejects_wrong_secret() -> None:
    """POST /api/report1/zip rejects requests with an incorrect shared secret."""
    response = client.post("/api/report1/zip", json=ZIP_PAYLOAD, headers=_auth_headers("wrong-secret"))

    assert response.status_code == 401


def test_generate_report1_zip_endpoint_returns_link(
    _stub_upload_report: list[tuple[bytes, str, str | None]],
) -> None:
    """POST /api/report1/zip uploads the ZIP and returns a JSON download link."""
    response = client.post("/api/report1/zip", json=ZIP_PAYLOAD, headers=_auth_headers())

    assert response.status_code == 200
    payload = response.json()
    assert payload["url"].startswith("https://stub.blob.core.windows.net/")
    assert payload["expires_at"] == "2026-01-01T00:00:00Z"

    # Exactly one upload happened, with ZIP bytes and the right extension.
    assert len(_stub_upload_report) == 1
    uploaded_bytes, extension, download_filename = _stub_upload_report[0]
    assert uploaded_bytes[:2] == b"PK"
    assert extension == "zip"
    assert download_filename is None


def test_generate_report1_zip_endpoint_forwards_filename(
    _stub_upload_report: list[tuple[bytes, str, str | None]],
) -> None:
    """POST /api/report1/zip forwards the requested filename to the blob upload."""
    payload = {**ZIP_PAYLOAD, "filename": "report 2025-11.zip"}
    response = client.post("/api/report1/zip", json=payload, headers=_auth_headers())

    assert response.status_code == 200
    assert len(_stub_upload_report) == 1
    _, _, download_filename = _stub_upload_report[0]
    assert download_filename == "report 2025-11.zip"


def test_generate_report1_pdf_requires_shared_secret() -> None:
    """POST /api/report1/pdf rejects requests without the shared-secret header."""
    response = client.post("/api/report1/pdf", json=PDF_PAYLOAD)

    assert response.status_code == 401


def test_generate_report1_pdf_endpoint_returns_link(
    _stub_upload_report: list[tuple[bytes, str, str | None]],
) -> None:
    """POST /api/report1/pdf uploads the PDF and returns a JSON download link."""
    response = client.post("/api/report1/pdf", json=PDF_PAYLOAD, headers=_auth_headers())

    assert response.status_code == 200
    payload = response.json()
    assert payload["url"].startswith("https://stub.blob.core.windows.net/")

    assert len(_stub_upload_report) == 1
    uploaded_bytes, extension, _ = _stub_upload_report[0]
    assert uploaded_bytes[:4] == b"%PDF"
    assert extension == "pdf"


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
