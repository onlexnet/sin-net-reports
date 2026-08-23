"""Blob storage integration for report1.

Generated reports (PDF/ZIP) are uploaded to a dedicated Azure Storage
container instead of being streamed back in the HTTP response body. A
time-limited SAS URL is returned to the caller instead; the underlying
blobs are removed automatically after 7 days by a storage account
lifecycle management policy (see infra/shared/module_fun_report1).
"""

from __future__ import annotations

import os
import uuid
from dataclasses import dataclass
from datetime import UTC, datetime, timedelta

from azure.identity import DefaultAzureCredential
from azure.storage.blob import BlobSasPermissions, BlobServiceClient, ContentSettings, generate_blob_sas

# The SAS link is deliberately shorter-lived than the blob retention period,
# so a leaked link expires long before the report itself is deleted.
SAS_VALIDITY = timedelta(hours=24)


@dataclass(frozen=True)
class ReportLink:
    """A downloadable link to a generated report and its expiry timestamp."""

    url: str
    expires_at: datetime


def _account_url(account_name: str) -> str:
    """Build the HTTPS endpoint for a storage account's blob service."""
    return f"https://{account_name}.blob.core.windows.net"


def _blob_service_client(account_name: str) -> BlobServiceClient:
    """Create a BlobServiceClient authenticated via the function's managed identity."""
    credential = DefaultAzureCredential()
    return BlobServiceClient(account_url=_account_url(account_name), credential=credential)


def upload_report(content: bytes, extension: str, download_filename: str | None = None) -> ReportLink:
    """Upload a generated report and return a 24h read-only SAS link to it.

    Parameters
    ----------
    content:
        Raw bytes of the generated report (PDF or ZIP).
    extension:
        File extension without a leading dot, e.g. "pdf" or "zip".
    download_filename:
        Optional, human-meaningful filename (e.g. "report 2025-11.zip") to present to the
        browser on download. The blob itself is still stored under a random, collision-free
        name; this value is only set as the blob's Content-Disposition header so that clients
        opening the SAS URL directly (without proxying through webapi) still get a meaningful
        filename instead of the random blob name.

    Returns
    -------
    ReportLink
        A publicly reachable, time-limited URL and its expiry timestamp.
    """
    account_name = os.environ["REPORT1_STORAGE_ACCOUNT_NAME"]
    container_name = os.environ["REPORT1_STORAGE_CONTAINER_NAME"]
    blob_name = f"report1/{uuid.uuid4()}.{extension}"
    content_settings = (
        ContentSettings(content_disposition=f'attachment; filename="{download_filename}"')
        if download_filename
        else None
    )

    service_client = _blob_service_client(account_name)
    container_client = service_client.get_container_client(container_name)
    container_client.upload_blob(
        name=blob_name, data=content, overwrite=False, content_settings=content_settings
    )

    now = datetime.now(UTC)
    expires_at = now + SAS_VALIDITY
    # User delegation SAS: no storage account key is ever read or stored;
    # the function's managed identity must hold the "Storage Blob Delegator"
    # role to obtain this key (see infra/shared/module_fun_report1).
    delegation_key = service_client.get_user_delegation_key(key_start_time=now, key_expiry_time=expires_at)
    sas_token = generate_blob_sas(
        account_name=account_name,
        container_name=container_name,
        blob_name=blob_name,
        user_delegation_key=delegation_key,
        permission=BlobSasPermissions(read=True),
        expiry=expires_at,
    )
    url = f"{_account_url(account_name)}/{container_name}/{blob_name}?{sas_token}"
    return ReportLink(url=url, expires_at=expires_at)
