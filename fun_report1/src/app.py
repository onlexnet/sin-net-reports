"""FastAPI application and route definitions for fun_report1."""

from __future__ import annotations

from fastapi import Depends, FastAPI

from src.auth import require_shared_secret
from src.models_generated import (
    HealthResponse,
    Status,
)
from src.models_report1 import ReportLinkResponse, ReportRequest, ReportRequests

# FastAPI application instance
fastapi_app = FastAPI(
    title="Fun Report1 API",
    description=("Azure Function app providing health checks and report generation"),
    version="1.0.0",
    openapi_url="/api/openapi.json",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
)


@fastapi_app.get(
    "/api/health",
    response_model=HealthResponse,
    tags=["Health"],
    summary="Health check endpoint",
    operation_id="getHealth",
)
async def health() -> HealthResponse:
    """Return a basic health response for smoke checks."""
    return HealthResponse(status=Status.ok, service="app_raport1")


@fastapi_app.post(
    "/api/report1/zip",
    tags=["Reports"],
    summary="Generate Raport miesięczny - załączniki do faktur",
    operation_id="generateReport1Zip",
    response_model=ReportLinkResponse,
    dependencies=[Depends(require_shared_secret)],
)
async def generate_report1_zip(request: ReportRequests) -> ReportLinkResponse:
    """Generate a ZIP archive of PDF invoice attachments and return a download link.

    The archive is uploaded to blob storage (retained for 7 days); the
    returned URL is a SAS link valid for 24 hours.
    """
    from src.blob_storage import upload_report
    from src.zip_generator import generate_zip

    zip_bytes = generate_zip(request)
    link = upload_report(zip_bytes, extension="zip", download_filename=request.filename)
    return ReportLinkResponse(url=link.url, expires_at=link.expires_at)


@fastapi_app.post(
    "/api/report1/pdf",
    tags=["Reports"],
    summary="Generate single PDF attachment for one customer",
    operation_id="generateReport1Pdf",
    response_model=ReportLinkResponse,
    dependencies=[Depends(require_shared_secret)],
)
async def generate_report1_pdf(request: ReportRequest) -> ReportLinkResponse:
    """Generate a single customer PDF and return a download link.

    Uses the same renderer as ZIP generation. The PDF is uploaded to blob
    storage (retained for 7 days); the returned URL is a SAS link valid for
    24 hours.
    """
    from src.blob_storage import upload_report
    from src.pdf_generator import generate_pdf

    pdf_bytes = generate_pdf(request)
    link = upload_report(pdf_bytes, extension="pdf")
    return ReportLinkResponse(url=link.url, expires_at=link.expires_at)
