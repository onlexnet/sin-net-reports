#!/usr/bin/env python3
"""Validate OpenAPI specification for fun_report1.

This script validates the OpenAPI schema located in res_schema/fun_report1.openapi/
against the OpenAPI 3.0 specification. It's used in local development and CI
to ensure the contract remains valid.
"""

from __future__ import annotations

import sys
from pathlib import Path

from openapi_spec_validator import validate_spec
from openapi_spec_validator.readers import read_from_filename


def main() -> int:
    """Validate the OpenAPI specification and return exit code."""
    schema_path = (
        Path(__file__).parent.parent.parent
        / "res_schema"
        / "fun_report1.openapi"
        / "openapi.yaml"
    )

    if not schema_path.exists():
        print(f"❌ Schema file not found: {schema_path}", file=sys.stderr)
        return 1

    print(f"Validating OpenAPI schema: {schema_path}")

    try:
        spec_dict, spec_url = read_from_filename(str(schema_path))
        validate_spec(spec_dict, spec_url=spec_url)
        print("✅ OpenAPI schema is valid")
        return 0
    except Exception as e:
        print(f"❌ Validation failed: {e}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
