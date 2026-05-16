"""Azure Functions app entrypoint for app_raport1.

Thin shim that registers the Azure Functions HTTP trigger and delegates
all requests to the FastAPI ASGI app defined in src.app.
"""

from __future__ import annotations

import azure.functions as func
from azure.functions import AsgiMiddleware

from src.app import fastapi_app

# Azure Functions HTTP trigger bridging requests to FastAPI.
app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)
asgi_middleware: AsgiMiddleware = AsgiMiddleware(fastapi_app)


@app.function_name(name="http_app_func")
@app.route(route="{*route}")
async def http_app_func(
    req: func.HttpRequest, context: func.Context
) -> func.HttpResponse:
    """Handle all HTTP routes by delegating to the FastAPI ASGI app."""
    return await asgi_middleware.handle_async(req, context)
