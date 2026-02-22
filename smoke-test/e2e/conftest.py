"""
============================================================================
Pytest Configuration and Fixtures
============================================================================

This module provides pytest fixtures and hooks for the E2E test suite.
Fixtures are reusable test setup/teardown code.

Key fixtures:
- test_context: Shared test context for passing data between steps
- browser: Configured Playwright browser instance

============================================================================
"""

import pytest
import os
import re
import base64
from html import escape
from typing import Any, Generator
from playwright.sync_api import Page


@pytest.fixture
def test_context() -> dict[str, Any]:
    """
    Provide a shared dictionary for passing data between test steps.
    
    In pytest-bdd, we can't use Cucumber's built-in context, so we
    create our own dictionary that gets injected into step functions.
    
    Usage in step definitions:
        def my_step(test_context):
            test_context['key'] = 'value'
            assert test_context['key'] == 'value'
    """
    return {}


@pytest.fixture(scope="session")
def browser_context_args(browser_context_args: dict[str, Any]) -> dict[str, Any]:
    """
    Configure browser context with custom settings.
    
    This overrides the default pytest-playwright browser context
    to add custom configuration like viewport size, user agent, etc.
    """
    return {
        **browser_context_args,
        # Set viewport size (useful for responsive testing)
        "viewport": {
            "width": 1920,
            "height": 1080,
        },
        # Ignore HTTPS certificate errors (for local development)
        "ignore_https_errors": True,
    }


@pytest.fixture(scope="function")
def page(page: Page) -> Generator[Page, None, None]:
    """
    Configure page-level settings for each test.
    
    This fixture is automatically provided by pytest-playwright.
    We can customize it here with timeouts, default navigation options, etc.
    """
    # Set default timeout for all page actions
    page.set_default_timeout(10000)  # 10 seconds
    
    # Set default navigation timeout (page loads)
    page.set_default_navigation_timeout(30000)  # 30 seconds
    
    yield page
    
    # Cleanup: Take screenshot on failure (handled by pytest-playwright)
    # No explicit cleanup needed here


def pytest_configure(config: Any) -> None:
    """
    Hook called before test collection.
    Used for custom pytest configuration.
    """
    # Create reports directory if it doesn't exist
    reports_dir = os.path.join(os.path.dirname(__file__), 'reports')
    os.makedirs(reports_dir, exist_ok=True)


def pytest_bdd_step_error(
    request: Any,
    feature: Any,
    scenario: Any,
    step: Any,
    step_func: Any,
    step_func_args: dict[str, Any],
    exception: Exception,
) -> None:
    """
    Hook called when a BDD step fails.
    Useful for custom error handling and debugging.
    """
    print(f"\n❌ Step failed: {step.name}")
    print(f"   Feature: {feature.name}")
    print(f"   Scenario: {scenario.name}")
    print(f"   Exception: {exception}")


def _safe_name(value: str) -> str:
    return re.sub(r"[^a-zA-Z0-9._-]+", "-", value).strip("-").lower()


@pytest.hookimpl(tryfirst=True)
def pytest_bdd_after_step(
    request: Any,
    feature: Any,
    scenario: Any,
    step: Any,
    step_func: Any,
    step_func_args: dict[str, Any],
) -> None:
    page = step_func_args.get("page")
    if page is None:
        return

    reports_dir = os.path.join(os.path.dirname(__file__), "reports", "screenshots")
    scenario_dir = os.path.join(
        reports_dir,
        _safe_name(feature.name),
        _safe_name(scenario.name),
    )
    os.makedirs(scenario_dir, exist_ok=True)

    step_index = getattr(step, "line_number", "step")
    screenshot_name = f"{step_index:03d}-{_safe_name(step.name)}.png" if isinstance(step_index, int) else f"{step_index}-{_safe_name(step.name)}.png"
    screenshot_path = os.path.join(scenario_dir, screenshot_name)

    page.screenshot(path=screenshot_path, full_page=True)


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item: Any, call: Any) -> Generator[None, None, None]:
    outcome = yield
    report = outcome.get_result()

    if report.when != "call" or report.passed:
        return

    pytest_html = item.config.pluginmanager.getplugin("html")
    if pytest_html is None:
        return

    extras = list(getattr(report, "extras", []))
    screenshot_paths: list[str] = []

    test_context = item.funcargs.get("test_context") if hasattr(item, "funcargs") else None
    if isinstance(test_context, dict):
        context_paths = test_context.get("failure_screenshots", [])
        if isinstance(context_paths, list):
            screenshot_paths.extend(path for path in context_paths if isinstance(path, str) and path)

    if not screenshot_paths:
        longrepr_text = getattr(report, "longreprtext", "")
        screenshot_paths.extend(re.findall(r"Screenshot saved at:\s*(.+)", longrepr_text))

    unique_paths = list(dict.fromkeys(screenshot_paths))
    base_dir = os.path.dirname(__file__)

    for screenshot_path in unique_paths:
        resolved_path = screenshot_path if os.path.isabs(screenshot_path) else os.path.abspath(os.path.join(base_dir, screenshot_path))
        if not os.path.exists(resolved_path):
            continue

        with open(resolved_path, "rb") as screenshot_file:
            image_base64 = base64.b64encode(screenshot_file.read()).decode("ascii")

        relative_path = os.path.relpath(resolved_path, base_dir)
        extras.append(
            pytest_html.extras.html(
                "<div>"
                f"<p><strong>Failure screenshot:</strong> {escape(relative_path)}</p>"
                f"<img src=\"data:image/png;base64,{image_base64}\" style=\"max-width: 100%; height: auto; border: 1px solid #ddd;\" />"
                "</div>"
            )
        )

    report.extras = extras
