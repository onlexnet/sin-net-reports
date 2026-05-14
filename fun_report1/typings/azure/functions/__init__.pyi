from collections.abc import Callable
from enum import Enum
from typing import Any, TypeVar

from ._http_asgi import AsgiMiddleware

F = TypeVar("F", bound=Callable[..., Any])

class HttpRequest: ...

class HttpResponse: ...

class Context: ...

class AuthLevel(Enum):
	ANONYMOUS = "ANONYMOUS"

class FunctionApp:
	def __init__(self, http_auth_level: AuthLevel | None = ...) -> None: ...
	def function_name(self, *, name: str) -> Callable[[F], F]: ...
	def route(
		self,
		*,
		route: str,
		methods: list[str] | None = ...,
		auth_level: AuthLevel | None = ...,
		trigger_arg_name: str = ...,
		binding_arg_name: str = ...,
	) -> Callable[[F], F]: ...

__all__ = [
	"AsgiMiddleware",
	"AuthLevel",
	"Context",
	"FunctionApp",
	"HttpRequest",
	"HttpResponse",
]
