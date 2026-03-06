"""
============================================================================
Step Definitions for static-webapp-time E2E Features
============================================================================

This module implements step definitions for the new shadcn-based webapp
(static-webapp-time) running on port 3001.

============================================================================
"""

import re
import os
from datetime import datetime, timezone
from typing import Any
from playwright.sync_api import Page, expect
from pytest_bdd import when, then, parsers, scenarios

TestContext = dict[str, Any]

# Load all scenarios from feature files
scenarios('../features/login_time.feature')

# ============================================================================
# Configuration
# ============================================================================

WEBAPP_TIME_URL = "http://localhost:3001"
UI_TIMEOUT_LONG = 60000
UI_TIMEOUT_SHORT = 20000


def _safe_name(value: str) -> str:
    return re.sub(r"[^a-zA-Z0-9._-]+", "-", value).strip("-").lower()


def _capture_failure_screenshot(page: Page, label: str, test_context: TestContext | None = None) -> str:
    """Capture a screenshot on failure."""
    reports_dir = os.path.abspath(
        os.path.join(os.path.dirname(__file__), "..", "reports", "screenshots", "assertions")
    )
    os.makedirs(reports_dir, exist_ok=True)
    timestamp = datetime.now(timezone.utc).strftime("%Y%m%d-%H%M%S-%f")
    screenshot_path = os.path.join(reports_dir, f"{timestamp}-{_safe_name(label)}.png")
    page.screenshot(path=screenshot_path, full_page=True)
    if test_context is not None:
        existing_paths = test_context.get('failure_screenshots')
        if not isinstance(existing_paths, list):
            existing_paths = []
            test_context['failure_screenshots'] = existing_paths
        existing_paths.append(screenshot_path)
    return screenshot_path


def _complete_project_selection_if_present(page: Page) -> None:
    """Handle project selection dialog if it appears after login."""
    continue_button = page.get_by_role('button', name='Kontynuuj pracę z wybranym projektem')
    if not continue_button.is_visible():
        return

    first_project = page.get_by_role('radio').first
    expect(first_project).to_be_visible(timeout=UI_TIMEOUT_LONG)
    first_project.check()
    expect(continue_button).to_be_enabled(timeout=UI_TIMEOUT_SHORT)
    continue_button.click()


# ============================================================================
# When Steps (Actions)
# ============================================================================

@when(parsers.parse('I login to the time webapp as "{email}"'))
def login_to_time_webapp(page: Page, test_context: TestContext, email: str) -> None:
    """Log in to the time webapp using the test login screen."""
    test_context['page_response'] = page.goto(WEBAPP_TIME_URL, wait_until='domcontentloaded', timeout=30000)

    expect(page.get_by_role('heading', name='Test Login')).to_be_visible(timeout=UI_TIMEOUT_LONG)
    expect(page.get_by_placeholder('Enter your email')).to_be_visible(timeout=UI_TIMEOUT_SHORT)
    page.get_by_placeholder('Enter your email').fill(email)
    page.get_by_role('button', name='Login').click()

    _complete_project_selection_if_present(page)
    expect(page.get_by_text('Usługi', exact=False)).to_be_visible(timeout=UI_TIMEOUT_LONG)
    expect(page.get_by_text('Klienci', exact=False)).to_be_visible(timeout=UI_TIMEOUT_LONG)

    test_context['page'] = page


# ============================================================================
# Then Steps (Assertions)
# ============================================================================

@then(parsers.parse('the time webapp page should contain "{expected_text}"'))
def verify_time_webapp_page_contains(test_context: TestContext, expected_text: str) -> None:
    """Verify that the time webapp page contains expected text."""
    page = test_context.get('page')
    assert page is not None, "No page object available"

    try:
        expect(page.get_by_text(expected_text, exact=False)).to_be_visible(timeout=UI_TIMEOUT_LONG)
    except AssertionError as e:
        screenshot_path = _capture_failure_screenshot(page, f"time-webapp-missing-text-{expected_text}", test_context)
        raise AssertionError(f"{e}\nScreenshot saved at: {screenshot_path}") from e


@then(parsers.parse('the time webapp menu should contain "{first_item}" and "{second_item}"'))
def verify_time_webapp_menu_items(test_context: TestContext, first_item: str, second_item: str) -> None:
    """Verify that the time webapp menu contains required navigation items."""
    page = test_context.get('page')
    assert page is not None, "No page object available"

    expect(page.get_by_text(first_item, exact=False)).to_be_visible(timeout=UI_TIMEOUT_LONG)
    expect(page.get_by_text(second_item, exact=False)).to_be_visible(timeout=UI_TIMEOUT_LONG)
