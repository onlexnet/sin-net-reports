"""
============================================================================
Pytest Configuration and Fixtures
============================================================================

This module provides pytest fixtures and hooks for the E2E test suite.
Fixtures are reusable test setup/teardown code.

Key fixtures:
- context: Shared test context for passing data between steps
- browser: Configured Playwright browser instance

============================================================================
"""

import pytest
import os
from playwright.sync_api import Page


@pytest.fixture
def context():
    """
    Provide a shared dictionary for passing data between test steps.
    
    In pytest-bdd, we can't use Cucumber's built-in context, so we
    create our own dictionary that gets injected into step functions.
    
    Usage in step definitions:
        def my_step(context):
            context['key'] = 'value'
            assert context['key'] == 'value'
    """
    return {}


@pytest.fixture(scope="session")
def browser_context_args(browser_context_args):
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
def page(page: Page):
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


def pytest_configure(config):
    """
    Hook called before test collection.
    Used for custom pytest configuration.
    """
    # Create reports directory if it doesn't exist
    reports_dir = os.path.join(os.path.dirname(__file__), 'reports')
    os.makedirs(reports_dir, exist_ok=True)


def pytest_bdd_step_error(request, feature, scenario, step, step_func, step_func_args, exception):
    """
    Hook called when a BDD step fails.
    Useful for custom error handling and debugging.
    """
    print(f"\n❌ Step failed: {step.name}")
    print(f"   Feature: {feature.name}")
    print(f"   Scenario: {scenario.name}")
    print(f"   Exception: {exception}")
