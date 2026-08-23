"""Unit tests for src.blob_storage.upload_report.

Azure SDK objects (DefaultAzureCredential, BlobServiceClient) are monkeypatched
so the tests never make real network/identity calls.
"""

from __future__ import annotations

from datetime import datetime
from typing import Any

import pytest

from src import blob_storage
from src.blob_storage import upload_report


class _FakeContainerClient:
    def __init__(self) -> None:
        self.upload_calls: list[dict[str, Any]] = []

    def upload_blob(self, **kwargs: Any) -> None:
        self.upload_calls.append(kwargs)


class _FakeServiceClient:
    def __init__(self) -> None:
        self.container_client = _FakeContainerClient()

    def get_container_client(self, container_name: str) -> _FakeContainerClient:
        return self.container_client

    def get_user_delegation_key(self, key_start_time: datetime, key_expiry_time: datetime) -> str:
        return "fake-delegation-key"


@pytest.fixture
def _fake_service_client(monkeypatch: pytest.MonkeyPatch) -> _FakeServiceClient:
    """Replace the Azure BlobServiceClient (and SAS generation) with in-memory fakes."""
    service_client = _FakeServiceClient()
    monkeypatch.setattr(blob_storage, "_blob_service_client", lambda account_name: service_client)
    monkeypatch.setattr(blob_storage, "generate_blob_sas", lambda **kwargs: "fake-sas-token")
    monkeypatch.setenv("REPORT1_STORAGE_ACCOUNT_NAME", "teststorage")
    monkeypatch.setenv("REPORT1_STORAGE_CONTAINER_NAME", "reports")
    return service_client


def test_upload_report_sets_content_disposition_when_filename_given(
    _fake_service_client: _FakeServiceClient,
) -> None:
    """A meaningful download filename is set as the blob's Content-Disposition header."""
    link = upload_report(b"zip-bytes", extension="zip", download_filename="report 2025-11.zip")

    upload_call = _fake_service_client.container_client.upload_calls[0]
    content_settings = upload_call["content_settings"]
    assert content_settings.content_disposition == 'attachment; filename="report 2025-11.zip"'
    assert link.url.startswith("https://teststorage.blob.core.windows.net/reports/report1/")


def test_upload_report_omits_content_disposition_when_no_filename_given(
    _fake_service_client: _FakeServiceClient,
) -> None:
    """No Content-Disposition is set when the caller doesn't provide a download filename."""
    upload_report(b"zip-bytes", extension="zip")

    upload_call = _fake_service_client.container_client.upload_calls[0]
    assert upload_call["content_settings"] is None
