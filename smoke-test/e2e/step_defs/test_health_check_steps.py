"""
============================================================================
Step Definitions for E2E Features
============================================================================

This module implements the step definitions for E2E feature files.
It uses pytest-bdd to map Gherkin steps to Python functions.

Step types:
- @given: Setup/preconditions
- @when: Actions/operations
- @then: Assertions/verifications

============================================================================
"""

import requests
from typing import Any
from playwright.sync_api import Page
from pytest_bdd import given, when, then, parsers, scenarios

TestContext = dict[str, Any]

# Load all scenarios from feature files
scenarios('../features')

# ============================================================================
# Configuration
# ============================================================================

WEBAPI_URL = "http://localhost:11031"
TIMEENTRIES_URL = "http://localhost:11021"
FRONTEND_URL = "http://localhost:3000"

# ============================================================================
# Given Steps (Preconditions)
# ============================================================================

@given("the k3d stack is running")
def k3d_stack_running() -> None:
    """
    Verify that k3d stack is running.
    This is a precondition check that can be extended to verify
    services are actually running.
    """
    # This step is mainly declarative - the actual check happens
    # when we try to connect to services in subsequent steps
    pass

# ============================================================================
# When Steps (Actions)
# ============================================================================

@when("I check the WebAPI health endpoint")
def check_webapi_health(test_context: TestContext) -> None:
    """
    Make HTTP request to WebAPI health endpoint.
    Spring Boot Actuator provides /actuator/health endpoint.
    """
    try:
        test_context['response'] = requests.get(
            f"{WEBAPI_URL}/actuator/health",
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        test_context['error'] = str(e)
        test_context['response'] = None

@when("I check the TimeEntries health endpoint")
def check_timeentries_health(test_context: TestContext) -> None:
    """
    Make HTTP request to TimeEntries health endpoint.
    Spring Boot Actuator provides /actuator/health endpoint.
    """
    try:
        test_context['response'] = requests.get(
            f"{TIMEENTRIES_URL}/actuator/health",
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        test_context['error'] = str(e)
        test_context['response'] = None

@when("I navigate to the frontend URL")
def navigate_to_frontend(page: Page, test_context: TestContext) -> None:
    """
    Use Playwright to navigate to the frontend application.
    The 'page' fixture is provided by pytest-playwright.
    """
    try:
        test_context['page_response'] = page.goto(FRONTEND_URL, timeout=10000)
        test_context['page'] = page
    except Exception as e:
        test_context['error'] = str(e)
        test_context['page_response'] = None

@when("I send a GraphQL introspection query")
def send_graphql_introspection(test_context: TestContext) -> None:
    """
    Send GraphQL introspection query to discover schema.
    This query is used by GraphQL tools to explore the API.
    """
    introspection_query = """
    query IntrospectionQuery {
        __schema {
            queryType { name }
            types {
                name
                kind
            }
        }
    }
    """
    
    try:
        test_context['response'] = requests.post(
            f"{WEBAPI_URL}/graphql",
            json={'query': introspection_query},
            headers={'Content-Type': 'application/json'},
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        test_context['error'] = str(e)
        test_context['response'] = None


@when(parsers.parse('I login as "{email}"'))
def login_as_user(page: Page, test_context: TestContext, email: str) -> None:
    """
    Log in using the email-only login screen.
    """
    try:
        test_context['page_response'] = page.goto(FRONTEND_URL, timeout=10000)
        page.locator('input').first.fill(email)
        page.get_by_role('button', name='Login').click()
        page.wait_for_load_state('networkidle')
        page.locator('body').get_by_text('Witaj w systemie ewidencji usług').wait_for(timeout=10000)
        test_context['page'] = page
    except Exception as e:
        test_context['error'] = str(e)
        test_context['page'] = page

# ============================================================================
# Then Steps (Assertions)
# ============================================================================

@then(parsers.parse("the WebAPI should respond with status {status_code:d}"))
def verify_webapi_status(test_context: TestContext, status_code: int) -> None:
    """
    Verify that WebAPI responded with expected HTTP status code.
    """
    assert test_context.get('response') is not None, \
        f"No response received. Error: {test_context.get('error', 'Unknown')}"
    assert test_context['response'].status_code == status_code, \
        f"Expected status {status_code}, got {test_context['response'].status_code}"

@then(parsers.parse("the TimeEntries service should respond with status {status_code:d}"))
def verify_timeentries_status(test_context: TestContext, status_code: int) -> None:
    """
    Verify that TimeEntries service responded with expected HTTP status code.
    """
    assert test_context.get('response') is not None, \
        f"No response received. Error: {test_context.get('error', 'Unknown')}"
    assert test_context['response'].status_code == status_code, \
        f"Expected status {status_code}, got {test_context['response'].status_code}"

@then(parsers.parse('the health status should be "{expected_status}"'))
def verify_health_status(test_context: TestContext, expected_status: str) -> None:
    """
    Verify that health endpoint reports expected status.
    Spring Boot Actuator health response: {"status": "UP"}
    """
    assert test_context.get('response') is not None, "No response received"
    health_data = test_context['response'].json()
    actual_status = health_data.get('status', 'UNKNOWN')
    assert actual_status == expected_status, \
        f"Expected health status '{expected_status}', got '{actual_status}'"

@then("the page should load successfully")
def verify_page_loaded(test_context: TestContext) -> None:
    """
    Verify that frontend page loaded without errors.
    """
    assert test_context.get('page_response') is not None, \
        f"Page failed to load. Error: {test_context.get('error', 'Unknown')}"
    assert test_context['page_response'].ok, \
        f"Page loaded with HTTP error: {test_context['page_response'].status}"

@then(parsers.parse('the page should contain "{expected_text}"'))
def verify_page_title(test_context: TestContext, expected_text: str) -> None:
    """
    Verify that page title contains expected text.
    """
    page = test_context.get('page')
    assert page is not None, "No page object available"
    
    title = page.title()
    if title:
        assert expected_text.lower() in title.lower(), \
            f"Expected page title to contain '{expected_text}', got '{title}'"
        return

    page_text = page.locator('body').inner_text()
    assert expected_text.lower() in page_text.lower(), \
        f"Expected page content to contain '{expected_text}', title was empty"

@then("the response should contain GraphQL schema information")
def verify_graphql_schema_present(test_context: TestContext) -> None:
    """
    Verify that GraphQL introspection query returned schema data.
    """
    assert test_context.get('response') is not None, "No response received"
    assert test_context['response'].status_code == 200, \
        f"GraphQL query failed with status {test_context['response'].status_code}"
    
    data = test_context['response'].json()
    assert 'data' in data, "Response does not contain 'data' field"
    assert '__schema' in data['data'], "Schema information not found in response"

@then(parsers.parse('the response should include "{type_name}" type'))
def verify_graphql_type_exists(test_context: TestContext, type_name: str) -> None:
    """
    Verify that GraphQL schema includes expected type.
    """
    data = test_context['response'].json()
    query_type = data['data']['__schema']['queryType']['name']
    assert query_type == type_name, \
        f"Expected query type '{type_name}', got '{query_type}'"


@then(parsers.parse('the menu should contain "{first_item}" and "{second_item}"'))
def verify_menu_items(test_context: TestContext, first_item: str, second_item: str) -> None:
    """
    Verify that the application menu contains required navigation items.
    """
    page = test_context.get('page')
    assert page is not None, "No page object available"

    page_text = page.locator('body').inner_text()
    assert first_item in page_text, f"Expected menu item '{first_item}' not found"
    assert second_item in page_text, f"Expected menu item '{second_item}' not found"
