# app_raport1

Minimal Azure Functions (Python v2) scaffold for the first public endpoint set.

## Runtime targets

- Azure Functions Python programming model v2
- Python 3.13
- Public HTTP routes (anonymous auth):
  - `/api/health`
  - `/api/report`

## Files

- `function_app.py` - function entrypoint with HTTP routes
- `host.json` - Azure Functions host configuration
- `requirements.txt` - Python dependencies
- `.funcignore` - files excluded from deployment package
- `local.settings.json.example` - safe local config template

## Local run

Prerequisites:
- Python 3.13
- Azure Functions Core Tools v4 (`func` command)
  install: *npm install -g azure-functions-core-tools@4 --unsafe-perm true*

Commands:

```bash
python3.13 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp local.settings.json.example local.settings.json
func start
```

When running locally, test endpoints:
- `http://localhost:7071/api/health`
- `http://localhost:7071/api/report`

## Notes

- Keep secrets only in `local.settings.json` (not committed).
- This scaffold is intended as a quality-first baseline for deployment.
