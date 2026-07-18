"""Unit tests for zip_generator.generate_zip.

All tests are self-contained: they build in-memory ReportRequests objects,
call generate_zip, and verify the returned bytes and ZIP structure without
touching the file system or any external services.
"""

from __future__ import annotations

import io
import zipfile

from src.models_report1 import ActivityDate, ActivityDetails, CustomerDetails, ReportRequest, ReportRequests
from src.zip_generator import generate_zip

# ---------------------------------------------------------------------------
# Fixtures / helpers
# ---------------------------------------------------------------------------


def _make_request(customer_name: str = "Acme Corp") -> ReportRequest:
    """Return a minimal ReportRequest for *customer_name*."""
    return ReportRequest(
        customer=CustomerDetails(
            customer_id="cust-001",
            customer_name=customer_name,
            customer_city="Kraków",
            customer_address="Główna 5",
        ),
        activities=[
            ActivityDetails(
                description="Maintenance",
                who="Jan Kowalski",
                when=ActivityDate(year=2024, month=7, day=1),
                how_long_in_mins=60,
                how_far_in_kms=10,
            )
        ],
    )


# ---------------------------------------------------------------------------
# Tests
# ---------------------------------------------------------------------------


def test_generate_zip_returns_bytes() -> None:
    """generate_zip returns bytes starting with the ZIP magic bytes 'PK'."""
    requests = ReportRequests(items=[_make_request("Acme Corp"), _make_request("Beta Ltd")])

    result = generate_zip(requests)

    assert isinstance(result, bytes)
    assert result[:2] == b"PK", "Must start with ZIP magic bytes"


def test_generate_zip_contains_correct_files() -> None:
    """ZIP contains correctly named files with slash-to-underscore sanitisation."""
    requests = ReportRequests(
        items=[
            _make_request("Acme Corp"),
            _make_request("Test/Co"),
        ]
    )

    result = generate_zip(requests)

    with zipfile.ZipFile(io.BytesIO(result)) as zf:
        names = zf.namelist()

    assert names == ["001-Acme Corp.pdf", "002-Test_Co.pdf"]


def test_generate_zip_files_are_valid_pdfs() -> None:
    """Each file extracted from the ZIP is a valid PDF (starts with %PDF)."""
    requests = ReportRequests(items=[_make_request("Acme Corp")])

    result = generate_zip(requests)

    with zipfile.ZipFile(io.BytesIO(result)) as zf:
        pdf_bytes = zf.read("001-Acme Corp.pdf")

    assert pdf_bytes[:4] == b"%PDF"


def test_generate_zip_empty_requests() -> None:
    """generate_zip with zero items returns a valid, empty ZIP archive."""
    requests = ReportRequests(items=[])

    result = generate_zip(requests)

    # Must be valid ZIP even when empty
    with zipfile.ZipFile(io.BytesIO(result)) as zf:
        assert zf.namelist() == []
