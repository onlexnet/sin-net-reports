"""ZIP archive generator for the 'Raport miesięczny - załączniki do faktur' report.

Iterates over a collection of per-customer ReportRequest objects, generates
one PDF per customer via pdf_generator.generate_pdf, and bundles them into
an in-memory ZIP archive.

Filename convention (mirrors Java Report1QueryPack.java):
    "{index:03d}-{customer_name}.pdf"
where index is 1-based and any "/" in customer_name is replaced with "_".
"""

from __future__ import annotations

import io
import re
import zipfile

from src.models_report1 import ReportRequests
from src.pdf_generator import generate_pdf


def _sanitize_filename_fragment(value: str) -> str:
    """Replace filesystem-unsafe characters with a pipe so ZIP entries stay portable."""
    sanitized = re.sub(r'[<>:"/\\|?*\x00-\x1f\x7f]', "|", value)
    return sanitized.strip(" .")


def generate_zip(requests: ReportRequests) -> bytes:
    """Generate a ZIP archive containing one PDF per customer.

    Parameters
    ----------
    requests:
        Collection of per-customer report requests.

    Returns
    -------
    bytes
        Raw ZIP bytes suitable for writing to a file or an HTTP response.
    """
    buffer = io.BytesIO()

    with zipfile.ZipFile(buffer, mode="w", compression=zipfile.ZIP_DEFLATED) as zf:
        for index, request in enumerate(requests.items, start=1):
            pdf_bytes = generate_pdf(request)
            safe_name = _sanitize_filename_fragment(request.customer.customer_name)
            filename = f"{index:03d}-{safe_name}.pdf"
            zf.writestr(filename, pdf_bytes)

    return buffer.getvalue()
