"""FastAPI application and route definitions for fun_report1."""

from __future__ import annotations

from fastapi import FastAPI
from fastapi.responses import Response

from src.models_generated import (
    HealthResponse,
    Status,
)
from src.models_report1 import ReportRequest, ReportRequests

# FastAPI application instance
fastapi_app = FastAPI(
    title="Fun Report1 API",
    description=(
        "Azure Function app providing health checks and report generation"
    ),
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
    response_class=Response,
    responses={
        200: {
            "content": {"application/zip": {}},
            "description": "ZIP archive with PDF attachments",
        }
    },
)
async def generate_report1_zip(request: ReportRequests) -> Response:
    """Generate ZIP archive with PDF invoice attachments for all customers."""
    from src.zip_generator import generate_zip

    zip_bytes = generate_zip(request)
    return Response(
        content=zip_bytes,
        media_type="application/zip",
        headers={"Content-Disposition": "attachment; filename=report1.zip"},
    )


@fastapi_app.post(
    "/api/report1/pdf",
    tags=["Reports"],
    summary="Generate single PDF attachment for one customer",
    operation_id="generateReport1Pdf",
    response_class=Response,
    responses={
        200: {
            "content": {"application/pdf": {}},
            "description": "Single PDF attachment for the provided customer",
        }
    },
)
async def generate_report1_pdf(request: ReportRequest) -> Response:
    """Generate a single customer PDF using the same renderer as ZIP generation."""
    from src.pdf_generator import generate_pdf

    pdf_bytes = generate_pdf(request)
    return Response(
        content=pdf_bytes,
        media_type="application/pdf",
        headers={"Content-Disposition": "attachment; filename=report1.pdf"},
    )
