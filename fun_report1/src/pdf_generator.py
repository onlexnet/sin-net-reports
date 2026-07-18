"""PDF generation for the 'Raport miesięczny - załączniki do faktur' report.

Produces a single-customer PDF that matches the layout of the Java reference
implementation (ReportResults.java):

  - Header:  CustomerName (bold) + ", " + City + " ul. " + Address (regular)
  - Table 1: timed activities (howLongInMins > 0) with column headers and a
             bold summary row (no border)
  - Table 2: untimed / extra-service activities (howLongInMins == 0) preceded
             by a bold "USŁUGI DODATKOWE" title row and a bold summary row
             (no border)

Column widths are proportionally scaled from the Java reference (225 units
total) to fit a 450 pt content width on an A4 portrait page.
"""

from __future__ import annotations

from io import BytesIO
from pathlib import Path
from typing import Any

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.platypus.flowables import Flowable

from src.models_report1 import ActivityDetails, ReportRequest

# ---------------------------------------------------------------------------
# Layout constants
# ---------------------------------------------------------------------------

_PAGE_WIDTH, _PAGE_HEIGHT = A4
_MARGIN = 36  # 0.5 inch margins on each side (in points)
_CONTENT_WIDTH = _PAGE_WIDTH - 2 * _MARGIN  # ~523 pt for A4

# Java reference column widths (sum = 225) → scaled to _CONTENT_WIDTH
_JAVA_COLS = [50, 25, 120, 15, 15]
_JAVA_TOTAL = sum(_JAVA_COLS)
_COL_WIDTHS = [w * _CONTENT_WIDTH / _JAVA_TOTAL for w in _JAVA_COLS]

_BASE_FONT_SIZE = 10
_SERVICEMAN_FONT_SIZE = 6  # base - 4 as per Java reference

_FONT_REGULAR = "DejaVuSans"
_FONT_BOLD = "DejaVuSans-Bold"
_FONT_REGULAR_PATH = Path("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf")
_FONT_BOLD_PATH = Path("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf")

_SPACER_HEIGHT = 12  # points


def _ensure_fonts_registered() -> None:
    """Register Unicode-capable fonts used in PDF rendering."""
    registered_fonts = set(pdfmetrics.getRegisteredFontNames())
    if _FONT_REGULAR not in registered_fonts:
        pdfmetrics.registerFont(TTFont(_FONT_REGULAR, str(_FONT_REGULAR_PATH)))
    if _FONT_BOLD not in registered_fonts:
        pdfmetrics.registerFont(TTFont(_FONT_BOLD, str(_FONT_BOLD_PATH)))

# ---------------------------------------------------------------------------
# Style helpers
# ---------------------------------------------------------------------------

_STYLES = getSampleStyleSheet()

_HEADER_BOLD = ParagraphStyle(
    "HeaderBold",
    parent=_STYLES["Normal"],
    fontName=_FONT_BOLD,
    fontSize=_BASE_FONT_SIZE,
    leading=14,
)

_HEADER_NORMAL = ParagraphStyle(
    "HeaderNormal",
    parent=_STYLES["Normal"],
    fontName=_FONT_REGULAR,
    fontSize=_BASE_FONT_SIZE,
    leading=14,
)

# Table cell styles
_CELL_NORMAL = ParagraphStyle(
    "CellNormal",
    parent=_STYLES["Normal"],
    fontName=_FONT_REGULAR,
    fontSize=_BASE_FONT_SIZE,
)

_CELL_BOLD = ParagraphStyle(
    "CellBold",
    parent=_STYLES["Normal"],
    fontName=_FONT_BOLD,
    fontSize=_BASE_FONT_SIZE,
)

_CELL_SERVICEMAN = ParagraphStyle(
    "CellServiceman",
    parent=_STYLES["Normal"],
    fontName=_FONT_REGULAR,
    fontSize=_SERVICEMAN_FONT_SIZE,
)


# ---------------------------------------------------------------------------
# Formatting helpers
# ---------------------------------------------------------------------------


def _format_duration(mins: int) -> str:
    """Format minutes as 'H:MM' (e.g. 90 → '1:30')."""
    hours = mins // 60
    remaining = mins % 60
    return f"{hours}:{remaining:02d}"


def _format_date(activity: ActivityDetails) -> str:
    """Format ActivityDate as 'dd-MM-yyyy', or empty string when absent."""
    if activity.when is None:
        return ""
    d = activity.when
    return f"{d.day:02d}-{d.month:02d}-{d.year:04d}"


# ---------------------------------------------------------------------------
# Table builders
# ---------------------------------------------------------------------------

_COL_HEADERS = ["Serwisant", "Dzień", "Praca wykonana", "Czas", "KM"]
type StyleCommand = tuple[Any, ...]

# TableStyle commands shared by both data tables
_GRID_STYLE: list[StyleCommand] = [
    ("FONTNAME", (0, 0), (-1, -1), _FONT_REGULAR),
    ("FONTSIZE", (0, 0), (-1, -1), _BASE_FONT_SIZE),
    ("FONTNAME", (0, 0), (0, -1), _FONT_REGULAR),
    ("FONTSIZE", (0, 0), (0, -1), _SERVICEMAN_FONT_SIZE),
    ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
]


def _header_row_style(row: int) -> list[StyleCommand]:
    """Return TableStyle commands that add a full grid border to *row*."""
    return [
        ("FONTNAME", (0, row), (-1, row), _FONT_BOLD),
        ("FONTSIZE", (0, row), (-1, row), _BASE_FONT_SIZE),
        ("BOX", (0, row), (-1, row), 0.5, "black"),
        ("INNERGRID", (0, row), (-1, row), 0.5, "black"),
    ]


def _data_row_style(row: int) -> list[StyleCommand]:
    """Return TableStyle commands that add a full grid border to a data *row*."""
    return [
        ("BOX", (0, row), (-1, row), 0.5, "black"),
        ("INNERGRID", (0, row), (-1, row), 0.5, "black"),
    ]


def _summary_row_style(row: int) -> list[StyleCommand]:
    """Return TableStyle commands for a bold, border-free summary *row*."""
    return [
        ("FONTNAME", (0, row), (-1, row), _FONT_BOLD),
        ("FONTSIZE", (0, row), (-1, row), _BASE_FONT_SIZE),
        # Explicitly no box/innergrid → no border
    ]


def _build_timed_table(timed: list[ActivityDetails]) -> Table:
    """Build the table for timed activities (howLongInMins > 0).

    Includes a column-header row, one data row per activity, and a bold
    summary row without borders.
    """
    rows: list[list[str]] = [_COL_HEADERS]
    total_mins = 0
    total_km = 0

    for act in timed:
        rows.append(
            [
                act.who,
                _format_date(act),
                act.description,
                _format_duration(act.how_long_in_mins),
                str(act.how_far_in_kms),
            ]
        )
        total_mins += act.how_long_in_mins
        total_km += act.how_far_in_kms

    # Summary row (bold, no border)
    rows.append(["", "", "Suma", _format_duration(total_mins), str(total_km)])

    style_cmds: list[StyleCommand] = list(_GRID_STYLE)
    style_cmds += _header_row_style(0)
    for idx in range(1, len(rows) - 1):
        style_cmds += _data_row_style(idx)
    style_cmds += _summary_row_style(len(rows) - 1)

    return Table(rows, colWidths=_COL_WIDTHS, style=TableStyle(style_cmds))


def _build_untimed_table(untimed: list[ActivityDetails]) -> Table:
    """Build the table for untimed / extra-service activities (howLongInMins == 0).

    Includes a full-width title row ("USŁUGI DODATKOWE"), a column-header row,
    one data row per activity, and a bold summary row without borders.
    """
    # Row 0: full-width title (span all columns, bold, no border)
    title_row: list[str] = ["USŁUGI DODATKOWE", "", "", "", ""]

    rows: list[list[str]] = [title_row, _COL_HEADERS]
    total_km = 0

    for act in untimed:
        rows.append(
            [
                act.who,
                _format_date(act),
                act.description,
                "0:00",
                str(act.how_far_in_kms),
            ]
        )
        total_km += act.how_far_in_kms

    rows.append(["", "", "Suma", "0:00", str(total_km)])

    style_cmds: list[StyleCommand] = list(_GRID_STYLE)

    # Title row: bold, spanning all columns, no border
    style_cmds += [
        ("FONTNAME", (0, 0), (-1, 0), _FONT_BOLD),
        ("FONTSIZE", (0, 0), (-1, 0), _BASE_FONT_SIZE),
        ("SPAN", (0, 0), (-1, 0)),
    ]

    # Column header row (row 1)
    style_cmds += _header_row_style(1)

    # Data rows (rows 2 … len-2)
    for idx in range(2, len(rows) - 1):
        style_cmds += _data_row_style(idx)

    # Summary row
    style_cmds += _summary_row_style(len(rows) - 1)

    return Table(rows, colWidths=_COL_WIDTHS, style=TableStyle(style_cmds))


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------


def generate_pdf(request: ReportRequest) -> bytes:
    """Generate a PDF for a single customer matching the Java reference layout.

    Parameters
    ----------
    request:
        Per-customer data containing customer details and activity list.

    Returns
    -------
    bytes
        Raw PDF bytes ready to be written to a file or HTTP response.
    """
    _ensure_fonts_registered()

    buffer = BytesIO()

    doc = SimpleDocTemplate(
        buffer,
        pagesize=A4,
        leftMargin=_MARGIN,
        rightMargin=_MARGIN,
        topMargin=_MARGIN,
        bottomMargin=_MARGIN,
    )

    # ------------------------------------------------------------------
    # Split activities into timed and untimed
    # ------------------------------------------------------------------
    timed = [a for a in request.activities if a.how_long_in_mins != 0]
    untimed = [a for a in request.activities if a.how_long_in_mins == 0]

    # ------------------------------------------------------------------
    # Customer header paragraph:
    #   "<CustomerName>" (bold) + ", <City> ul. <Address>" (regular)
    # ------------------------------------------------------------------
    cust = request.customer
    header_text = (
        f'<b>{cust.customer_name}</b>'
        f', {cust.customer_city} ul. {cust.customer_address}'
    )
    header_para = Paragraph(header_text, _HEADER_NORMAL)

    story: list[Flowable] = [
        header_para,
        Spacer(1, _SPACER_HEIGHT),
        _build_timed_table(timed),
        Spacer(1, _SPACER_HEIGHT),
        _build_untimed_table(untimed),
        Spacer(1, _SPACER_HEIGHT),
    ]

    doc.build(story)
    return buffer.getvalue()
