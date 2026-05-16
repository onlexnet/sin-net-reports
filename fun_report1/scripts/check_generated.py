#!/usr/bin/env python3
"""Check for drift between generated models and OpenAPI specification.

This script ensures that the generated Pydantic models in models_generated.py
are up-to-date with the OpenAPI schema. It regenerates models to a temporary
file and compares with the committed version.

This check should be run in CI to prevent drift between contract and code.
"""

from __future__ import annotations

import subprocess
import sys
import tempfile
from difflib import unified_diff
from pathlib import Path


def normalize_content(content: str) -> list[str]:
    """Normalize file content for comparison.

    Removes timestamp comments and other non-deterministic content
    that may differ between generations.
    """
    lines = content.splitlines(keepends=True)
    normalized = []
    for line in lines:
        # Skip timestamp lines that change on each generation
        if "timestamp:" in line:
            continue
        normalized.append(line)
    return normalized


def main() -> int:
    """Check if generated models match the OpenAPI spec."""
    project_root = Path(__file__).parent.parent
    schema_path = (
        project_root.parent
        / "schema"
        / "fun_report1.openapi"
        / "openapi.yaml"
    )
    current_models = project_root / "src" / "models_generated.py"

    if not schema_path.exists():
        print(f"❌ Schema file not found: {schema_path}", file=sys.stderr)
        return 1

    if not current_models.exists():
        print(
            f"❌ Generated models not found: {current_models}",
            file=sys.stderr,
        )
        print("Run: python scripts/generate_models.py", file=sys.stderr)
        return 1

    # Generate models to temporary file
    with tempfile.NamedTemporaryFile(
        mode="w", suffix=".py", delete=False
    ) as temp_file:
        temp_path = Path(temp_file.name)

    try:
        print(f"Generating fresh models from: {schema_path}")
        cmd = [
            "datamodel-codegen",
            "--input",
            str(schema_path),
            "--output",
            str(temp_path),
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

        subprocess.run(
            cmd, check=True, capture_output=True, text=True
        )

        # Read and normalize both files
        current_content = normalize_content(current_models.read_text())
        fresh_content = normalize_content(temp_path.read_text())

        # Compare normalized content
        if current_content != fresh_content:
            print("❌ Generated models are out of sync with OpenAPI spec!")
            print()
            print("Diff (current vs fresh):")
            print()
            diff = unified_diff(
                current_content,
                fresh_content,
                fromfile="models_generated.py (current)",
                tofile="models_generated.py (fresh)",
                lineterm="",
            )
            print("\n".join(diff))
            print()
            print("To fix, run: python scripts/generate_models.py")
            return 1

        print("✅ Generated models are in sync with OpenAPI spec")
        return 0

    except subprocess.CalledProcessError as e:
        print(f"❌ Generation failed: {e}", file=sys.stderr)
        if e.stderr:
            print(e.stderr, file=sys.stderr)
        return 1
    finally:
        # Clean up temp file
        if temp_path.exists():
            temp_path.unlink()


if __name__ == "__main__":
    sys.exit(main())
