"""Shared-secret authentication for internal callers of report1 endpoints.

fun_report1 is invoked by webapi (and, optionally, directly by trusted
operators). Instead of Azure Functions host-level keys, a simple shared
secret is used: both services receive the same value as an app
setting/environment variable (provisioned by Terraform), and callers must
present it in a custom header on every report-generation request.
"""

from __future__ import annotations

import os
import secrets

from fastapi import Header, HTTPException, status

SHARED_SECRET_HEADER = "X-Report1-Secret"


async def require_shared_secret(
    x_report1_secret: str | None = Header(default=None, alias=SHARED_SECRET_HEADER),
) -> None:
    """Reject requests that do not present the expected shared secret.

    Comparison uses ``secrets.compare_digest`` to avoid leaking information
    about the secret through response-time side channels.

    Raises
    ------
    HTTPException
        401 if the header is missing or does not match; 500 if the server
        itself has no secret configured (fail closed rather than allowing
        anonymous access by accident).
    """
    expected = os.environ.get("REPORT1_SHARED_SECRET")
    if not expected:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Server misconfiguration: REPORT1_SHARED_SECRET is not set",
        )
    if not x_report1_secret or not secrets.compare_digest(x_report1_secret, expected):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing or invalid shared secret",
        )
