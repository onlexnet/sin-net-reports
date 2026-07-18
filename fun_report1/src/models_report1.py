"""Pydantic v2 models for the report1 input data.

These models mirror the protobuf definitions in reports1.proto and are used
as FastAPI request bodies for the report generation endpoints.
"""

from __future__ import annotations

from pydantic import BaseModel, ConfigDict, Field


class ActivityDate(BaseModel):
    """A calendar date composed of year, month and day components."""

    year: int = Field(..., description="Full calendar year, e.g. 2024")
    month: int = Field(..., ge=1, le=12, description="Month of year (1–12)")
    day: int = Field(..., ge=1, le=31, description="Day of month (1–31)")


class ActivityDetails(BaseModel):
    """A single service activity performed for a customer."""

    description: str = Field(..., description="Human-readable description of the work")
    who: str = Field(..., description="Serviceman / technician name")
    when: ActivityDate | None = Field(None, description="Date the work was performed")
    how_long_in_mins: int = Field(0, ge=0, description="Duration in minutes; 0 means untimed")
    how_far_in_kms: int = Field(0, ge=0, description="Distance travelled in kilometres")


class CustomerDetails(BaseModel):
    """Identifying and address information for a customer."""

    customer_id: str = Field(..., description="Unique customer identifier")
    customer_name: str = Field(..., description="Display name of the customer")
    customer_city: str = Field(..., description="City where the customer is located")
    customer_address: str = Field(..., description="Street address of the customer")


class ReportRequest(BaseModel):
    """All data required to produce one customer's PDF attachment."""

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "customer": {
                    "customer_id": "cust-001",
                    "customer_name": "Zaklad Instalacji Sanitarnych Kowalski",
                    "customer_city": "Warszawa",
                    "customer_address": "ul. Prosta 12/4",
                },
                "activities": [
                    {
                        "description": "Przeglad i naprawa pompy",
                        "who": "Marek Zolc",
                        "when": {"year": 2026, "month": 7, "day": 10},
                        "how_long_in_mins": 135,
                        "how_far_in_kms": 24,
                    },
                    {
                        "description": "Dostawa czesci zamiennych",
                        "who": "Anna Nowak",
                        "when": {"year": 2026, "month": 7, "day": 11},
                        "how_long_in_mins": 0,
                        "how_far_in_kms": 18,
                    },
                ],
            }
        }
    )

    customer: CustomerDetails = Field(..., description="Customer identification details")
    activities: list[ActivityDetails] = Field(
        default_factory=list, description="List of service activities for this customer"
    )


class ReportRequests(BaseModel):
    """Collection of per-customer report requests bundled into one ZIP job."""

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "items": [
                    {
                        "customer": {
                            "customer_id": "cust-001",
                            "customer_name": "Zaklad Instalacji Sanitarnych Kowalski",
                            "customer_city": "Warszawa",
                            "customer_address": "ul. Prosta 12/4",
                        },
                        "activities": [
                            {
                                "description": "Przeglad i naprawa pompy",
                                "who": "Marek Zolc",
                                "when": {"year": 2026, "month": 7, "day": 10},
                                "how_long_in_mins": 135,
                                "how_far_in_kms": 24,
                            },
                            {
                                "description": "Dostawa czesci zamiennych",
                                "who": "Anna Nowak",
                                "when": {"year": 2026, "month": 7, "day": 11},
                                "how_long_in_mins": 0,
                                "how_far_in_kms": 18,
                            },
                        ],
                    },
                    {
                        "customer": {
                            "customer_id": "cust-002",
                            "customer_name": "Biuro Rachunkowe Delta",
                            "customer_city": "Lodz",
                            "customer_address": "ul. Zielona 8",
                        },
                        "activities": [
                            {
                                "description": "Konfiguracja sieci biurowej",
                                "who": "Piotr Wisniewski",
                                "when": {"year": 2026, "month": 7, "day": 12},
                                "how_long_in_mins": 90,
                                "how_far_in_kms": 12,
                            }
                        ],
                    },
                ]
            }
        }
    )

    items: list[ReportRequest] = Field(
        default_factory=list, description="One entry per customer to include in the ZIP"
    )
