# fun_report1

**Contract-first Azure Functions app** providing health checks and report generation.  
Built with **FastAPI + OpenAPI** for automatic validation and interactive documentation.

## Architecture

This service follows a **contract-first approach**:
1. OpenAPI specification defines the API contract (`../schema/fun_report1.openapi/openapi.yaml`)
2. Pydantic models are **generated** from the schema (`models_generated.py`)
3. FastAPI endpoints use generated models for request/response validation
4. Azure Functions ASGI wrapper enables deployment to Azure Functions

**Key principles:**
- Schema is the single source of truth
- Never edit `models_generated.py` manually
- CI checks enforce sync between schema and generated code

## Runtime targets

- **FastAPI** - modern Python web framework with automatic OpenAPI generation
- **Azure Functions Python v2** - serverless deployment with ASGI adapter
- **Python 3.13**
- **Pydantic v2** - data validation using Python type hints

## Public endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check for monitoring |
| `/report` | GET | Sample report generation |
| `/api/docs` | GET | Interactive Swagger UI |
| `/api/redoc` | GET | Alternative API documentation |
| `/api/openapi.json` | GET | OpenAPI specification (JSON) |

## Files

### Application code
- `function_app.py` - FastAPI app with endpoint implementations
- `models_generated.py` - **Generated** Pydantic models (do not edit!)
- `host.json` - Azure Functions host configuration
- `requirements.txt` - Runtime Python dependencies
- `requirements-dev.txt` - Development/testing dependencies

### OpenAPI contract
- `../schema/fun_report1.openapi/openapi.yaml` - **Source of truth** API contract

### Scripts
- `scripts/validate_openapi.py` - Validate OpenAPI schema
- `scripts/generate_models.py` - Generate Pydantic models from schema
- `scripts/check_generated.py` - Check for drift between schema and generated code

### Configuration
- `.funcignore` - Files excluded from deployment
- `local.settings.json.example` - Local config template
- `pyproject.toml` - Python tooling configuration (pytest, ruff, mypy)

## Development workflow

### Prerequisites
- Python 3.13
- Azure Functions Core Tools v4 (`func` command)
  ```bash
  npm install -g azure-functions-core-tools@4 --unsafe-perm true
  ```

### Initial setup
```bash
python3.13 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt -r requirements-dev.txt
cp local.settings.json.example local.settings.json
```

### Contract-first development cycle

#### 1. Update OpenAPI schema
Edit the API contract:
```bash
# Edit the OpenAPI specification
code ../schema/fun_report1.openapi/openapi.yaml
```

#### 2. Validate schema
```bash
python scripts/validate_openapi.py
```

#### 3. Generate models
```bash
python scripts/generate_models.py
```

This regenerates `models_generated.py` with Pydantic models matching the schema.

#### 4. Update endpoint implementations
Edit `function_app.py` to use the new/updated models.

#### 5. Run tests
```bash
pytest
```

#### 6. Check for drift
```bash
python scripts/check_generated.py
```

This ensures generated code is in sync with the schema (critical for CI).

### Local testing

Start the development server:
```bash
func start
```

Test endpoints:
- Health: http://localhost:7071/health
- Report: http://localhost:7071/report
- Swagger UI: http://localhost:7071/api/docs
- ReDoc: http://localhost:7071/api/redoc

### Code quality checks

```bash
# Type checking
mypy --config-file mypy.ini

# Linting
ruff check .

# Formatting
ruff format .

# All checks + tests
pytest && mypy --config-file mypy.ini && ruff check .
```

## CI/CD integration

The CI pipeline **must** include these checks:

```bash
# 1. Validate OpenAPI schema
python scripts/validate_openapi.py

# 2. Check for drift (fails if generated code is out of sync)
python scripts/check_generated.py

# 3. Run tests
pytest --junitxml=reports/junit.xml --cov-report=xml:reports/coverage.xml

# 4. Type checking
mypy

# 5. Linting
ruff check .
```

**Important:** If `check_generated.py` fails, run `python scripts/generate_models.py` locally and commit the updated `models_generated.py`.

## Schema versioning

When making **breaking changes** to the OpenAPI schema:
1. Update `info.version` in `openapi.yaml` (semver)
2. Document migration path for API consumers
3. Consider API versioning strategy (e.g., `/v2/report`)

## Troubleshooting

### "Generated models are out of sync"
Run `python scripts/generate_models.py` and commit the changes.

### Type errors in endpoint implementations
Ensure you're using the correct field names from generated models:
- Use **camelCase** aliases when constructing models (e.g., `reportId=...`)
- JSON serialization automatically uses camelCase

### FastAPI route not found
Check that route paths in `function_app.py` match those in `openapi.yaml`.

### Azure Functions deployment fails
Ensure `requirements.txt` includes all runtime dependencies (not just dev).

## Notes

- **Never edit** `models_generated.py` manually - regenerate from schema
- Keep secrets only in `local.settings.json` (not committed)
- OpenAPI schema is the contract - update schema first, then code
- Use `ruff format` for consistent code style
