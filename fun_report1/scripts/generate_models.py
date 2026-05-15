#!/usr/bin/env python3
"""Generate Pydantic models from OpenAPI specification.

This script generates Python Pydantic models from the OpenAPI schema.
Generated models are used for request/response validation in FastAPI endpoints.
"""

from __future__ import annotations

import subprocess
import sys
from pathlib import Path


def main() -> int:
    """Generate Pydantic models from OpenAPI spec."""
    project_root = Path(__file__).parent.parent
    schema_path = (
        project_root.parent / "schema" / "fun_report1.openapi" / "openapi.yaml"
    )
    output_path = project_root / "models_generated.py"

    if not schema_path.exists():
        print(f"❌ Schema file not found: {schema_path}", file=sys.stderr)
        return 1

    print(f"Generating models from: {schema_path}")
    print(f"Output: {output_path}")

    cmd = [
        "datamodel-codegen",
        "--input",
        str(schema_path),
        "--output",
        str(output_path),
        "--input-file-type",
        "openapi",
        "--output-model-type",
        "pydantic_v2.BaseModel",
        "--field-constraints",
        "--use-standard-collections",
        "--use-schema-description",
        "--use-title-as-name",
        "--snake-case-field",
        "--strict-nullable",
        "--target-python-version",
        "3.13",
    ]

    try:
        result = subprocess.run(cmd, check=True, capture_output=True, text=True)
        if result.stdout:
            print(result.stdout)
        print("✅ Models generated successfully")
        return 0
    except subprocess.CalledProcessError as e:
        print(f"❌ Generation failed: {e}", file=sys.stderr)
        if e.stderr:
            print(e.stderr, file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
