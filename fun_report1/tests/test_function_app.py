"""Unit tests for HTTP endpoints in function_app."""

from __future__ import annotations

import json
from collections.abc import Sequence
from typing import Any, Protocol

import azure.functions as func

import function_app


class _HasTrigger(Protocol):
    """Protocol for Azure Functions metadata entries exposing a trigger."""

    def get_trigger(self) -> Any:
        """Return trigger metadata object."""


def _make_request(method: str = "GET") -> func.HttpRequest:
    """Create a minimal HttpRequest for endpoint unit testing."""
    return func.HttpRequest(
        method=method,
        url=f"http://localhost:7071/api/{method.lower()}",
        headers={},
        params={},
        route_params={},
        body=b"",
    )


def _route_trigger(route: str, functions: Sequence[_HasTrigger]) -> object:
    """Return trigger metadata for a route from a single function snapshot."""
    for function in functions:
        trigger = function.get_trigger()
        if getattr(trigger, "route", None) == route:
            return trigger
    raise AssertionError(f"No route trigger found for {route}")


def test_health_endpoint_success() -> None:
    """Health endpoint returns expected status and payload."""
    response = function_app.health(_make_request("GET"))

    assert response.status_code == 200
    assert response.mimetype == "application/json"
    payload = json.loads(response.get_body().decode("utf-8"))
    assert payload == {"status": "ok", "service": "app_raport1"}


def test_report_endpoint_success() -> None:
    """Report endpoint returns expected top-level structure."""
    response = function_app.report(_make_request("GET"))

    assert response.status_code == 200
    payload = json.loads(response.get_body().decode("utf-8"))
    assert payload["reportId"] == "sample-001"
    assert payload["currency"] == "PLN"
    assert payload["summary"]["entries"] == 5
    assert len(payload["items"]) == 5


def test_report_content_is_deterministic() -> None:
    """Report payload stays deterministic across repeated calls."""
    first_response = function_app.report(_make_request("GET"))
    second_response = function_app.report(_make_request("GET"))

    first_payload = json.loads(first_response.get_body().decode("utf-8"))
    second_payload = json.loads(second_response.get_body().decode("utf-8"))

    assert first_payload == second_payload
    assert first_payload["generatedAt"] == "2026-01-01T00:00:00Z"
    assert sum(item["amount"] for item in first_payload["items"]) == first_payload[
        "summary"
    ]["billableAmount"]


def test_routes_are_get_only() -> None:
    """Registered route metadata allows only GET requests."""
    functions = function_app.app.get_functions()
    health_trigger = _route_trigger("health", functions)
    report_trigger = _route_trigger("report", functions)

    health_methods = [
        getattr(method, "value", str(method))
        for method in getattr(health_trigger, "methods", [])
    ]
    report_methods = [
        getattr(method, "value", str(method))
        for method in getattr(report_trigger, "methods", [])
    ]

    assert health_methods == ["GET"]
    assert report_methods == ["GET"]
