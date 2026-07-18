"""Unit tests for pdf_generator.generate_pdf.

All tests are self-contained: they build in-memory ReportRequest objects,
call generate_pdf, and verify the returned bytes without touching the file
system or any external services.

PDF text content is checked by decompressing the content streams embedded in
the PDF.  ReportLab applies ASCII85Decode + FlateDecode by default, so each
stream is ASCII85-decoded and then zlib-decompressed.
"""

from __future__ import annotations

import base64
import re
import zlib

from src.models_report1 import ActivityDate, ActivityDetails, CustomerDetails, ReportRequest
from src.pdf_generator import generate_pdf

# ---------------------------------------------------------------------------
# Fixtures / helpers
# ---------------------------------------------------------------------------


def _make_customer(name: str = "Test Firma") -> CustomerDetails:
    """Return a minimal CustomerDetails instance."""
    return CustomerDetails(
        customer_id="cust-001",
        customer_name=name,
        customer_city="Warszawa",
        customer_address="Testowa 1",
    )


def _make_timed_activity(mins: int = 90, kms: int = 15) -> ActivityDetails:
    """Return a timed activity (howLongInMins > 0)."""
    return ActivityDetails(
        description="Naprawa serwera",
        who="Jan Kowalski",
        when=ActivityDate(year=2024, month=6, day=15),
        how_long_in_mins=mins,
        how_far_in_kms=kms,
    )


def _make_untimed_activity(kms: int = 10) -> ActivityDetails:
    """Return an untimed / extra-service activity (howLongInMins == 0)."""
    return ActivityDetails(
        description="Konsultacja telefoniczna",
        who="Anna Nowak",
        when=ActivityDate(year=2024, month=6, day=16),
        how_long_in_mins=0,
        how_far_in_kms=kms,
    )


def _extract_pdf_text(pdf_bytes: bytes) -> str:
    """Extract readable text from a PDF by decoding its content streams.

    ReportLab encodes streams with ASCII85Decode + FlateDecode by default.
    This helper iterates over every stream, attempts ASCII85 + zlib decoding,
    and falls back to raw latin-1 for any stream that cannot be decoded.
    """
    raw_streams = re.findall(b"stream\r?\n(.+?)endstream", pdf_bytes, re.DOTALL)
    parts: list[str] = []
    for raw in raw_streams:
        text = ""
        # Try ASCII85 → zlib (ReportLab default)
        try:
            a85_decoded = base64.a85decode(raw, adobe=True)
            text = zlib.decompress(a85_decoded).decode("latin-1", errors="replace")
        except Exception:
            pass
        # Try plain zlib (compress=0 or older builds)
        if not text:
            try:
                text = zlib.decompress(raw).decode("latin-1", errors="replace")
            except Exception:
                text = raw.decode("latin-1", errors="replace")
        parts.append(text)
    return "".join(parts)


# ---------------------------------------------------------------------------
# Tests
# ---------------------------------------------------------------------------


def test_generate_pdf_returns_bytes() -> None:
    """generate_pdf returns non-empty bytes with a valid PDF signature."""
    request = ReportRequest(
        customer=_make_customer(),
        activities=[
            _make_timed_activity(),
            _make_timed_activity(mins=60, kms=5),
            _make_untimed_activity(),
        ],
    )

    result = generate_pdf(request)

    assert isinstance(result, bytes)
    assert result[:4] == b"%PDF", "Must start with PDF magic bytes"
    assert len(result) > 100


def test_generate_pdf_contains_customer_name() -> None:
    """Generated PDF contains the customer name in its content stream."""
    customer_name = "Test Firma"
    request = ReportRequest(
        customer=_make_customer(customer_name),
        activities=[_make_timed_activity()],
    )

    result = generate_pdf(request)
    content_str = _extract_pdf_text(result)

    assert customer_name in content_str


def test_generate_pdf_empty_activities() -> None:
    """generate_pdf handles a request with zero activities without raising."""
    request = ReportRequest(
        customer=_make_customer(),
        activities=[],
    )

    result = generate_pdf(request)

    assert isinstance(result, bytes)
    assert result[:4] == b"%PDF"


def test_generate_pdf_timed_activity_appears() -> None:
    """Duration '1:30' and distance '15' appear in the PDF for a 90-min/15-km activity."""
    request = ReportRequest(
        customer=_make_customer(),
        activities=[_make_timed_activity(mins=90, kms=15)],
    )

    result = generate_pdf(request)
    content_str = _extract_pdf_text(result)

    assert "1:30" in content_str
    assert "15" in content_str


def test_generate_pdf_untimed_activity_appears() -> None:
    """'USŁUGI DODATKOWE' section is embedded for an untimed activity."""
    request = ReportRequest(
        customer=_make_customer(),
        activities=[_make_untimed_activity()],
    )

    result = generate_pdf(request)
    content_str = _extract_pdf_text(result)

    # "USŁUGI DODATKOWE" — the ASCII portion "DODATKOWE" is safe to check
    assert "DODATKOWE" in content_str
